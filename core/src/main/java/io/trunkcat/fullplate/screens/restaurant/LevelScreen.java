/*
 * Copyright (c) 2024-2025 Trunk Cat Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.trunkcat.fullplate.screens.restaurant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.trunkcat.fullplate.components.common.System;
import io.trunkcat.fullplate.components.debug.DebugStage;
import io.trunkcat.fullplate.components.kitchen.CustomerSystem;
import io.trunkcat.fullplate.components.kitchen.RecipeCollection;
import io.trunkcat.fullplate.components.kitchen.Seat;
import io.trunkcat.fullplate.components.utensils.BunCrate;
import io.trunkcat.fullplate.components.utensils.Pan;
import io.trunkcat.fullplate.components.utensils.PattyCrate;
import io.trunkcat.fullplate.components.utensils.Plate;
import io.trunkcat.fullplate.models.responses.Place;
import io.trunkcat.fullplate.models.responses.PlayerData;
import io.trunkcat.fullplate.network.ResponseHandler;
import io.trunkcat.fullplate.screens.BaseScreen;
import io.trunkcat.fullplate.screens.CustomStage;
import io.trunkcat.fullplate.screens.ScreenID;
import io.trunkcat.fullplate.screens.home.HomeScreen;
import io.trunkcat.fullplate.utilities.WidgetFactory;

// todo: Extract hud and kitchen
public class LevelScreen extends BaseScreen {
	private final CustomStage hudStage;
	private final CustomStage levelStage;
	private final Place.Level level;
	private final Map<String, Place.Level.Goal> levelGoals;
	private final LevelProgress levelProgress;
	private final DebugStage debugStage;

	private Label coinsLabel;

	public LevelScreen(Place.Level level) {
		super(ScreenID.LEVEL_SCREEN);
		this.level = level;
		this.levelGoals = level.getGoals()
		                       .stream()
		                       .collect(Collectors.toMap(
				                       Place.Level.Goal::getGoalType,
				                       goal -> goal
		                       ));
		if (!levelGoals.containsKey("coins")) {
			throw new Error("Coins goal should exist for every level.");
		}

		hudStage = new CustomStage("HUD Stage", new ScreenViewport());
		levelStage = new CustomStage("Restaurant Stage", new ScreenViewport());
		Viewport viewport = levelStage.getViewport();

		// TODO: make the background adaptive:
		Image backgroundImage = new Image(
				new Texture(Gdx.files.internal("restaurants/burger-place/background.png")));
		if (backgroundImage.getHeight() < viewport.getWorldHeight()
				|| backgroundImage.getWidth() < viewport.getWorldWidth()) {
			float aspectRatio = backgroundImage.getWidth() / backgroundImage.getHeight();
			backgroundImage.setHeight(viewport.getWorldHeight());
			backgroundImage.setWidth(viewport.getWorldHeight() * (aspectRatio));
		}
		backgroundImage.setPosition(
				(viewport.getWorldWidth() - backgroundImage.getWidth()) / 2f, 0);

		levelStage.setDebugAll(true);

		levelProgress = new LevelProgress();
		levelStage.addListener(new LevelEventListener(level, levelProgress) {
			@Override
			public boolean handle(Event event) {
				boolean handled = super.handle(event);

				if (event instanceof LevelEvent) {
					if (event instanceof LevelEvent.LevelCompletedEvent) {
						boolean won =
								levelProgress.getCoins() >= levelGoals.get("coins").getGoalValue();

						Table content = new Table();

						Table main = new Table();
						content.add(main).fill().expand().grow().left();
						content.row();

						main.defaults().expandX().fillX().left();

						main.add(
								new Label("Coins collected: " + levelProgress.getCoins(), game.skin)
						).row();
						main.add(
								new Label("Tip collected: " + levelProgress.getTip(), game.skin)
						).row();

						Table footer = new Table();
						footer.defaults().space(20f);
						content.add(footer).padTop(20f).fill().expand().grow();

						if (!won) {
							TextButton homeButton = new TextButton("Go Home", game.skin);
							footer.add(homeButton).fill().expand().grow();
							onClick(
									homeButton, () -> {
										game.setScreen(new HomeScreen());
									}
							);

							TextButton retryButton = new TextButton("Retry Level", game.skin);
							footer.add(retryButton).fill().expand().grow();
							onClick(
									retryButton, () -> {
										game.setScreen(new LevelScreen(level));
									}
							);
						} else {
							TextButton continueButton = new TextButton("Saving...", game.skin);
							footer.add(continueButton).fill().expand().grow();
							continueButton.setDisabled(true);

							HashMap<String, Object> data = new HashMap<>();
							data.put("placeId", level.getPlaceId());
							data.put("levelId", level.getLevelId());
							HashMap<Integer, Integer> goalProgress = new HashMap<>();
							for (Place.Level.Goal levelGoal : level.getGoals()) {
								goalProgress.put(
										levelGoal.getGoalId(),
										getObtainedGoalValue(levelGoal.getGoalType())
								);
							}
							data.put("goals", goalProgress);

							game.httpClient.put(
									"/player/goal",
									data,
									new ResponseHandler<Array<PlayerData.GoalProgress>>() {
										@Override
										public void success(Array<PlayerData.GoalProgress> response) {
											PlayerData.UnlockedPlace unlockedPlace = game.player
													.getUnlockedPlace(level.getPlaceId());
											PlayerData.CompletedLevel completedLevel = unlockedPlace.getCompletedLevel(
													level.getLevelId());

											ArrayList<PlayerData.GoalProgress> updatedGoalProgresses = new ArrayList<>();
											for (PlayerData.GoalProgress progress : new Array.ArrayIterator<>(
													response)) {
												updatedGoalProgresses.add(progress);
											}

											if (completedLevel == null) {
												PlayerData.CompletedLevel newlyCompletedLevel = new PlayerData.CompletedLevel();
												newlyCompletedLevel
														.setLevelId(level.getLevelId());
												newlyCompletedLevel
														.setGoalProgresses(updatedGoalProgresses);
												unlockedPlace
														.getCompletedLevels()
														.add(newlyCompletedLevel);
											} else {
												completedLevel.setGoalProgresses(
														updatedGoalProgresses);
											}

											continueButton.setText("Continue");
											continueButton.setDisabled(false);
										}

										@Override
										public void failure(String message) {
											// todo: show a toast
											Gdx.app.log("kek", message);
										}
									},
									PlayerData.GoalProgress.class, true
							);

							onClick(
									continueButton, () -> {
										game.setScreen(new HomeScreen(level.getPlaceId()));
									}
							);
						}

						WidgetFactory.WindowProps props = new WidgetFactory.WindowProps();
						props.setCloseButton(false);
						props.setPosition(hudStage.getWidth() / 2f, hudStage.getHeight() / 2f);
						props.setTitle("Level " + (won ? "won!" : "failed"));
						props.setDescription(won ? "Congratulations!" : "Better luck next time!");
						props.setMinWidth(hudStage.getWidth() / 4);

						Window window = WidgetFactory.createWindow(
								game.skin.get(Window.WindowStyle.class), content, props, hudStage
						);
						hudStage.addActor(window);
					}
				}

				return handled;
			}
		});

		debugStage = new DebugStage(levelStage, hudStage);

		setupHUD();
		setupKitchen();
	}

	private int getObtainedGoalValue(String goalType) {
		switch (goalType) {
			case "coins":
				return levelProgress.getCoins();
			case "tip":
				return levelProgress.getTip();
			case "xp":
				return levelProgress.getExperiencePoints();
			case "customers":
				return levelProgress.getCustomersServed();
			default:
				throw new IllegalArgumentException("Unknown goal type: " + goalType);
		}
	}

	@Override
	public void show() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(debugStage);
		inputMultiplexer.addProcessor(hudStage);
		inputMultiplexer.addProcessor(levelStage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// TODO: Extract hud and kitchen
		coinsLabel.setText(getCoinsText());

		levelStage.act(delta);
		levelStage.draw();

		hudStage.act(delta);
		hudStage.draw();

		debugStage.act(delta);
		debugStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		hudStage
				.getViewport()
				.update(width, height, true);
		levelStage
				.getViewport()
				.update(width, height, true);
		debugStage
				.getViewport()
				.update(width, height, true);
	}

	@Override
	public void dispose() {
		debugStage.dispose();
		hudStage.dispose();
		levelStage.dispose();
	}

	private String getCoinsText() {
		int coinsCollected = levelProgress.getCoins() + levelProgress.getTip();
		return coinsCollected + " coins";
	}

	private void setupHUD() {
		Table mainTable = new Table();
		mainTable.setFillParent(true);

		Table topBar = new Table();
		Table centerElements = new Table();

		coinsLabel = new Label(getCoinsText(), game.skin, "h2");
		centerElements.add(coinsLabel).space(15);

		topBar.add(centerElements).expandX().center();

		mainTable.top();
		mainTable.add(topBar).fillX().expandX();

		hudStage.addActor(mainTable);
	}

	private void setupKitchen() {
		BunCrate bunCrate = new BunCrate(1000);
		addLevelEntity(bunCrate, 100, 100);

		PattyCrate pattyCrate = new PattyCrate(1000);
		addLevelEntity(pattyCrate, 300, 100);

		Pan pan = new Pan();
		addLevelEntity(pan, 500, 100);

		Plate plate = new Plate();
		addLevelEntity(plate, 800, 100);

		CustomerSystem customerSystem = new CustomerSystem(
				RecipeCollection.from(Plate.RECIPE_COLLECTION),
				levelGoals,
				levelProgress
		);

		// todo: calculate this based on world width, customer actor width, and y pos.
		customerSystem.addSeat(new Seat(100, 700));
		customerSystem.addSeat(new Seat(500, 700));
		customerSystem.addSeat(new Seat(900, 700));
		customerSystem.addSeat(new Seat(1300, 700));

		addLevelSystem(customerSystem);
	}

	private void addLevelEntity(Actor item, int x, int y) {
		item.setPosition(x, y);
		levelStage.addActor(item);
	}

	private void addLevelSystem(System system) {
		system.setVisible(false);
		levelStage.addActor(system);
	}
}
