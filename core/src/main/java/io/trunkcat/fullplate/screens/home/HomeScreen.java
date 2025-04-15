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

package io.trunkcat.fullplate.screens.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.trunkcat.fullplate.models.responses.Place;
import io.trunkcat.fullplate.screens.BaseScreen;
import io.trunkcat.fullplate.screens.ScreenID;
import io.trunkcat.fullplate.utilities.WidgetFactory;

public class HomeScreen extends BaseScreen {
	private final Stage hudStage;
	private final Stage mapStage;
	private final MapGestureListener mapGestureHandler;

	private final Window exitConfirmationWindow;

	public HomeScreen() {
		super(ScreenID.HOME_SCREEN);

		hudStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		setupHUD();

		OrthographicCamera mapCamera = new OrthographicCamera();
		FitViewport mapViewport = new FitViewport(
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapCamera);
		mapStage = new Stage(mapViewport);
		Image mapImage = new Image(new Texture(Gdx.files.internal("backgrounds/map-ref.png")));

		game.audioManager.playMusic("music/bgmusic.ogg", true);

		mapImage.setPosition(0, 0);
		if (mapImage.getHeight() < mapViewport.getWorldHeight()
				|| mapImage.getWidth() < mapViewport.getWorldWidth()) {
			float aspectRatio = mapImage.getWidth() / mapImage.getHeight();
			mapImage.setHeight(mapViewport.getWorldHeight() * aspectRatio);
			mapImage.setWidth(mapViewport.getWorldWidth() * aspectRatio);
		}
		mapCamera.position.set(
				mapViewport.getWorldWidth() / 2f, mapViewport.getWorldHeight() / 2f, 0f
		);

		mapStage.addActor(mapImage);
		mapGestureHandler = new MapGestureListener(
				mapCamera, mapImage.getWidth(), mapImage.getHeight());

		setupMapPlaces(game.data.getPlaces());

		Table exitWindowContent = new Table();
		TextButton exitButton = new TextButton("Exit", game.skin);
		onChange(exitButton, () -> Gdx.app.exit());
		exitWindowContent.add(exitButton).spaceLeft(10f).fill().expand().grow().left();
		WidgetFactory.WindowProps props = new WidgetFactory.WindowProps();
		props.setTitle("Are you sure?");
		props.setDescription("All unsaved progress will be lost.");
		props.setCloseButton(true);
		props.setMargin(15f);
		props.setSpaceY(30f);
		props.setPosition(hudStage.getWidth() / 2f, hudStage.getHeight() / 2f);
		exitConfirmationWindow = WidgetFactory.createWindow(
				game.skin.get(Window.WindowStyle.class),
				exitWindowContent, props, hudStage
		);
	}

	@Override
	public void show() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setCatchKey(Input.Keys.BACK, true);
		inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.BACK) {
					if (exitConfirmationWindow.getStage() != null) {
						exitConfirmationWindow.remove();
					} else {
						hudStage.addActor(exitConfirmationWindow);
					}
					return true;
				}
				return false;
			}
		});
		inputMultiplexer.addProcessor(hudStage);
		inputMultiplexer.addProcessor(mapStage);
		inputMultiplexer.addProcessor(new GestureDetector(mapGestureHandler));
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapStage.act(delta);
		mapStage.draw();

		hudStage.act(delta);
		hudStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		hudStage.getViewport().update(width, height, true);
		mapStage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		hudStage.dispose();
		mapStage.dispose();
	}

	private void setupHUD() {
		Table mainTable = new Table();
		mainTable.setFillParent(true);

		// TOP BAR
		Table topBar = new Table();
		topBar.setBackground(game.skin.getDrawable("rect-top"));

		Table leftElements = new Table();
		Table centerElements = new Table();
		Table rightElements = new Table();

		Label levelLabel = new Label(
				"Lvl " + game.player.getStats().getPlayerLevel(), game.skin, "player-level");
		leftElements.add(levelLabel).left().padLeft(10).spaceRight(10);

		Label playerNameLabel = new Label(game.player.getUsername(), game.skin);
		Label playerIdLabel = new Label("#" + game.player.getPlayerId(), game.skin, "font-16");
		Table playerInfoVerticalGroup = new Table();
		playerInfoVerticalGroup.add(playerNameLabel).left().row();
		playerInfoVerticalGroup.add(playerIdLabel).left();
		leftElements
				.add(playerInfoVerticalGroup)
				.left()
				.spaceLeft(10);

		Label experiencePointsLabel = new Label(
				game.player.getStats().getExperiencePoints() + " xp", game.skin);
		centerElements.add(experiencePointsLabel).space(15);
		Label coinsLabel = new Label(game.player.getStats().getCoins() + " coins", game.skin);
		centerElements.add(coinsLabel).space(15);

		Button settingsButton = new Button(game.skin, "settings-button");
		settingsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showSettingsWindow();
			}
		});
		rightElements.add(settingsButton).size(64, 64).right().padRight(15).pad(5);

		topBar.add(leftElements).padLeft(15).expandX().left();
		topBar.add(centerElements).expandX().center();
		topBar.add(rightElements).padRight(15).expandX().right();

		// BOTTOM BAR
		Table bottomBar = new Table();
		bottomBar.setBackground(game.skin.getDrawable("rect-bottom"));

		TextButton achievementsButton = new TextButton("Achievements", game.skin);
		bottomBar.add(achievementsButton).pad(5).padLeft(15).height(60).left();
		TextButton inventoryButton = new TextButton("Inventory", game.skin);
		bottomBar.add(inventoryButton).pad(5).padLeft(15).height(60).expandX().left();
		TextButton minionsButton = new TextButton("Minions", game.skin);
		bottomBar.add(minionsButton).pad(5).padRight(15).height(60).right();

		// TODO: SLightly update the width of each button to make it look better (hack).
		//  Fix this by actually fixing the padding on the source skin styles.
		bottomBar.layout();
		for (Cell<?> children : new Array.ArrayIterator<>(bottomBar.getCells())) {
			if (children.getActor() instanceof TextButton) {
				children.width(children.getPrefWidth() + 30f);
			}
		}

		mainTable.top();
		mainTable.add(topBar).fillX().expandX();
		mainTable.row();
		mainTable.add().expandY(); // empty space in the middle
		mainTable.row();
		mainTable.add(bottomBar).fillX().expandX();

		hudStage.addActor(mainTable);
	}

	private void setupMapPlaces(Array<Place> places) {
		Group placesGroup = new Group();
		for (Place placeData : new Array.ArrayIterator<>(places)) {
			PlacePointer pointer = new PlacePointer(placeData, hudStage);
			placesGroup.addActor(pointer);
		}
		mapStage.addActor(placesGroup);
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
		table.pack();
		for (Cell<?> cell : new Array.ArrayIterator<>(table.getCells())) {
			size.x += cell.getPrefWidth();
			size.y += cell.getPrefHeight();
		}
		return size;
	}

	// TODO: Complete settings window
	private void showSettingsWindow() {
		Window window = createWindow();
		Table content = new Table();
		content.defaults().pad(10).expandX();

		//TODO: Load settings if player has a save (preferences)

		Label heading = new Label("Settings", game.skin, "h1");
		content.add(heading).expandX().left();
		content.row();

		Label audioLabel = new Label("Audio", game.skin, "h2");
		content.add(audioLabel).left().padBottom(10);
		content.row();

		Table audioControls = new Table();
		audioControls.left();
		showAudioControls(audioControls);
		content.add(audioControls).left();
		content.row();

		Label accountLabel = new Label("Account", game.skin, "h2");
		Label accountInfo = new Label(
				"Currently logged in as " + game.player.getUsername(), game.skin);

		TextButton logoutButton = new TextButton("Log Out", game.skin);

		content.add(accountLabel).left();
		content.row();
		content.add(accountInfo).left();
		content.row();
		content.add(logoutButton).left();
		content.row();

		TextButton closeButton = new TextButton("Close", game.skin);
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				window.remove();
			}
		});
		content.add(closeButton).pad(10);

		setWindowContent(window, content);
		hudStage.addActor(window);
	}

	private void showAudioControls(Table audioControls) {
		float musicVolume = game.preferences.getFloat("musicVolume");
		float soundVolume = game.preferences.getFloat("soundVolume");

		Label musicLabel = new Label("MUSIC", game.skin);
		Label soundLabel = new Label("SFX", game.skin);

		Slider musicSlider = new Slider(0.0f, 1.0f, 0.01f, false, game.skin);
		musicSlider.setValue(game.audioManager.getMusicVolume());
		musicSlider.setScale(2);

		Slider soundSlider = new Slider(0.0f, 1.0f, 0.01f, false, game.skin);
		soundSlider.setValue(game.audioManager.getSoundVolume());
		soundSlider.setScale(2);

		musicSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float musicVolume = musicSlider.getValue();
				game.audioManager.setMusicVolume(musicVolume);
			}
		});

		soundSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float soundVolume = soundSlider.getValue();
				game.audioManager.setSoundVolume(soundVolume);
			}
		});

		Button muteButton = new Button(game.skin, "volume-button");
		muteButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.audioManager.muteMusic(!game.audioManager.isMusicMuted());
				game.audioManager.muteSound(!game.audioManager.isSoundMuted());
			}
		});

		audioControls.add(musicLabel).expandX().padRight(50).width(70);
		audioControls.add(musicSlider).expandX().width(150);
		audioControls.row().left().pad(15);
		audioControls.add(soundLabel).expandX().padRight(50).width(70);
		audioControls.add(soundSlider).expandX().width(150).pad(15);
		audioControls.row().left();
		audioControls.add(muteButton).expandX().size(64).pad(15);
		audioControls.row().left();
	}
}
