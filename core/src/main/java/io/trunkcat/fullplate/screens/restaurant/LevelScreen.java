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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.trunkcat.fullplate.components.common.System;
import io.trunkcat.fullplate.components.debug.DebugStage;
import io.trunkcat.fullplate.components.kitchen.CustomerSystem;
import io.trunkcat.fullplate.components.kitchen.RecipeCollection;
import io.trunkcat.fullplate.components.kitchen.Seat;
import io.trunkcat.fullplate.components.utensils.BunCrate;
import io.trunkcat.fullplate.components.utensils.Pan;
import io.trunkcat.fullplate.components.utensils.PattyCrate;
import io.trunkcat.fullplate.components.utensils.Plate;
import io.trunkcat.fullplate.screens.BaseScreen;
import io.trunkcat.fullplate.screens.CustomStage;
import io.trunkcat.fullplate.screens.ScreenID;

// TODO: Extract hud and kitchen
public class LevelScreen extends BaseScreen {
	private final CustomStage hudStage;
	private final CustomStage levelStage;
	private final LevelData levelData;
	private final LevelProgress levelProgress;
	private final DebugStage debugStage;

	// HUD elements
	private Label experiencePointsLabel;
	private Label coinsLabel;

	public LevelScreen() {
		super(ScreenID.LEVEL_SCREEN);

		// Make level data passed on from the constructor parameters.
		levelData = new LevelData(2, 1000);

		hudStage = new CustomStage("HUD Stage", new ScreenViewport());

		levelStage = new CustomStage("Restaurant Stage", new ScreenViewport());
		Viewport viewport = levelStage.getViewport();

		// TODO: make the background adaptive:
		Image backgroundImage = new Image(
				new Texture(Gdx.files.internal("backgrounds/restaurants/burger-place.png")));
		if (backgroundImage.getHeight() < viewport.getWorldHeight()
				|| backgroundImage.getWidth() < viewport.getWorldWidth()) {
			float aspectRatio = backgroundImage.getWidth() / backgroundImage.getHeight();
			backgroundImage.setHeight(viewport.getWorldHeight());
			backgroundImage.setWidth(viewport.getWorldHeight() * (aspectRatio));
		}
		backgroundImage.setPosition(
				(viewport.getWorldWidth() - backgroundImage.getWidth()) / 2f, 0);
//        levelStage.addActor(backgroundImage);

/*
        Image tableImage = new Image(new Texture(Gdx.files.internal("restaurants/table.png")));
        float tableImageAspectRatio = tableImage.getHeight() / tableImage.getWidth();
        tableImage.setWidth(viewport.getWorldWidth());
        tableImage.setHeight(viewport.getWorldWidth() * tableImageAspectRatio);
        tableImage.addAction(Actions.alpha(0.1f));
        levelStage.addActor(tableImage);
*/

		levelStage.setDebugAll(true);

		levelProgress = new LevelProgress();
		levelStage.addListener(new LevelEventListener(levelData, levelProgress) {
			@Override
			public boolean handle(Event event) {
				boolean handled = super.handle(event);

				if (event instanceof LevelEvent) {
					if (event instanceof LevelEvent.LevelCompletedEvent) {
						Window window = createWindow();

						Table content = new Table();

						boolean won = levelProgress.getCoins() >= levelData.getCoinsGoal();
						String title = "LEVEL " + (won ? "WON" : "FAILED");

						content.add(new Label(title, game.skin, "h1"));

						setWindowContent(window, content);
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

	@Override
	public void show() {
		Gdx.app.log(
				"Stage", "Viewport world size: " + levelStage.getViewport().getWorldWidth() +
						"x" + levelStage.getViewport().getWorldHeight() +
						", screen size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight()
		);

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
		experiencePointsLabel.setText(getExperiencePointsText());

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
	}

	@Override
	public void dispose() {
		debugStage.dispose();
		hudStage.dispose();
		levelStage.dispose();
	}

	// FIXME: shouldn't be this. abstract or extract.
	private String getCoinsText() {
		int coinsCollected = levelProgress.getCoins() + levelProgress.getTip();
		return coinsCollected + " / " + levelData.getCoinsGoal() + " coins";
	}

	private String getExperiencePointsText() {
		return levelProgress.getExperiencePoints() + " xp";
	}

	private void setupHUD() {
		Table mainTable = new Table();
		mainTable.setFillParent(true);

		// TODO: make all these custom actors or make helper string builders
		Table topBar = new Table();
		Table centerElements = new Table();

		experiencePointsLabel = new Label(getExperiencePointsText(), game.skin, "h2");
		centerElements.add(experiencePointsLabel).space(15).padRight(30);
		coinsLabel = new Label(getCoinsText(), game.skin, "h2");
		centerElements.add(coinsLabel).space(15);

		topBar.add(centerElements).expandX().center();

		mainTable.top();
		mainTable.add(topBar).fillX().expandX();

		hudStage.addActor(mainTable);
	}

	private Window createWindow() {
		Window window = new Window("", game.skin);
		window.setMovable(false);
		window.setModal(true);
		window.setKeepWithinStage(true);
		window.setResizable(false);
		return window;
	}

	private void setWindowContent(Window window, Table content) {
		Vector2 tableSize = calculateTableSize(content);
		float windowWidth = tableSize.x + window.getStyle().background.getMinWidth() + 100;
		float windowHeight = tableSize.y + window.getStyle().background.getMinHeight() + 100;
		window.setSize(windowWidth, windowHeight);
		window.setPosition(
				Gdx.graphics.getWidth() / 2f - windowWidth / 2f,
				Gdx.graphics.getHeight() / 2f - windowHeight / 2f
		);
		window.add(content).expand().fill();
	}

	private Vector2 calculateTableSize(Table table) {
		Vector2 size = new Vector2();
		table.layout();
		for (Cell<?> cell : new Array.ArrayIterable<>(table.getCells())) {
			size.x += cell.getPrefWidth();
			size.y += cell.getPrefHeight();
		}
		return size;
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
				levelData,
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
