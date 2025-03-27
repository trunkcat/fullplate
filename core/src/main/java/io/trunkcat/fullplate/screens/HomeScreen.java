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

package io.trunkcat.fullplate.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.trunkcat.fullplate.CookGame;

class MapGestureHandler extends GestureDetector.GestureAdapter {
    private final OrthographicCamera camera;
    public static float INITIAL_ZOOM = 1f;
    private final Vector2 boundaries;

    MapGestureHandler(OrthographicCamera camera, float boundaryX, float boundaryY) {
        this.camera = camera;
        camera.zoom = INITIAL_ZOOM;
        this.boundaries = new Vector2(boundaryX, boundaryY);
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Vector2 delta = new Vector2(-deltaX * camera.zoom, deltaY * camera.zoom);
        Vector2 dest = new Vector2(camera.position.x + delta.x, camera.position.y + delta.y);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        if (dest.x < effectiveViewportWidth / 2f || dest.x > boundaries.x - effectiveViewportWidth / 2) {
            delta.x = 0;
        }
        if (dest.y < effectiveViewportHeight / 2f || dest.y > boundaries.y - effectiveViewportHeight / 2) {
            delta.y = 0;
        }
        camera.translate(delta);
        return true;
    }

    // TODO: implement and finish zoom
    @Override
    public boolean zoom(float initialDistance, float distance) {
//        Vector2 pointA = new Vector2(Gdx.input.getX(0), Gdx.input.getY(0));
//        Vector2 pointB = new Vector2(Gdx.input.getX(1), Gdx.input.getY(1));
//        Vector2 zoomCenter = new Vector2((pointA.x + pointB.x) / 2f, (pointA.y + pointB.y) / 2f);
//
//        float zoomFactor = camera.zoom * (initialDistance / distance);
//        zoomFactor = Math.max(0.5f, Math.min(Math.min(boundaries.x / camera.viewportWidth, boundaries.y / camera.viewportHeight), zoomFactor));
//        Gdx.app.log("Camera", zoomFactor + " " + camera.viewportWidth / boundaries.x);
//        camera.zoom = zoomFactor;
//
////        float targetZoom = camera.zoom * (initialDistance / distance);
////        float maxZoom = boundaries.x / camera.viewportWidth * targetZoom;
////        targetZoom = Math.max(0.5f, Math.min(maxZoom, targetZoom));
////        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, 0.05f);
////        camera.zoom = Math.max(0.5f, Math.min(maxZoom, camera.zoom));
        return true;
    }
}

public class HomeScreen implements com.badlogic.gdx.Screen {
    private final CookGame game;

    private final Stage hudStage;

    private final Stage mapStage;
    private final OrthographicCamera mapCamera;

    private final MapGestureHandler mapGestureHandler;

    public HomeScreen() {
        game = CookGame.getInstance();

        hudStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setupHUD();

        mapCamera = new OrthographicCamera();
        FitViewport mapViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapCamera);
        mapStage = new Stage(mapViewport);
        Image mapImage = new Image(new Texture(Gdx.files.internal("backgrounds/map-ref.png")));

        // TODO: update this position to the current location the player is in when loaded
        mapImage.setPosition(0, 0);
        if (mapImage.getHeight() < mapViewport.getWorldHeight() || mapImage.getWidth() < mapViewport.getWorldWidth()) {
            float aspectRatio = mapImage.getWidth() / mapImage.getHeight();
            mapImage.setHeight(mapViewport.getWorldHeight() * aspectRatio);
            mapImage.setWidth(mapViewport.getWorldWidth() * aspectRatio);
        }
        mapCamera.position.set(mapViewport.getWorldWidth() / 2f, mapViewport.getWorldHeight() / 2f, 0f); // TODO: figure this out

        mapStage.addActor(mapImage);
        mapGestureHandler = new MapGestureHandler(mapCamera, mapImage.getWidth(), mapImage.getHeight());
    }


    @Override
    public void show() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hudStage);
        inputMultiplexer.addProcessor(mapStage);
        inputMultiplexer.addProcessor(new GestureDetector(mapGestureHandler));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw the map stage first
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

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

        Label levelLabel = new Label("Lvl " + game.player.data.getStats().getPlayerLevel(), game.skin, "player-level");
        leftElements.add(levelLabel).left().padLeft(10).spaceRight(10);

        Label playerNameLabel = new Label(game.player.data.getUsername(), game.skin);
        Label playerIdLabel = new Label("#" + game.player.data.getId(), game.skin, "font-16");
        Table playerInfoVerticalGroup = new Table();
        playerInfoVerticalGroup.add(playerNameLabel).left().row();
        playerInfoVerticalGroup.add(playerIdLabel).left();
        leftElements.add(playerInfoVerticalGroup).left().spaceLeft(10);

        Label experiencePointsLabel = new Label(game.player.data.getStats().getExperiencePoints() + " xp", game.skin);
        centerElements.add(experiencePointsLabel).space(15);
        Label coinsLabel = new Label(game.player.data.getStats().getCoins() + " coins", game.skin);
        centerElements.add(coinsLabel).space(15);

        Button leaderboardButton = new Button(game.skin, "leaderboard-button");
        rightElements.add(leaderboardButton).size(64, 64).right().padRight(15).pad(5);
        Button notificationsButton = new Button(game.skin, "notifications-button");
        rightElements.add(notificationsButton).size(64, 64).right().padRight(15).pad(5);
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
        for (Cell children : bottomBar.getCells()) {
            children.width(children.getPrefWidth() + 30f);
        }

        mainTable.top();
        mainTable.add(topBar).fillX().expandX();
        mainTable.row();
        mainTable.add().expandY(); // empty space in the middle
        mainTable.row();
        mainTable.add(bottomBar).fillX().expandX();

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
        Gdx.app.log("size", "" + tableSize + " " + window.getStyle().background.getMinWidth() + " " + window.getStyle().background.getMinHeight());
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
        for (Cell cell : table.getCells()) {
            size.x += cell.getPrefWidth();
            size.y += cell.getPrefHeight();
        }
        return size;
    }

    // TODO: Complete settings window
    private void showSettingsWindow() {
        Window window = createWindow();
        Table content = new Table();
        content.defaults().pad(10).fillX();

        Label heading = new Label("Settings", game.skin, "h1");
        content.add(heading).expandX().left();
        content.row();

        Label audioLabel = new Label("Audio", game.skin, "h2");
        content.add(audioLabel).expandX().left();
        content.row();

        Label accountLabel = new Label("Account", game.skin, "h2");
        Label accountInfo = new Label("Currently logged in as " + game.player.data.getUsername(), game.skin);
        content.add(accountLabel).left();
        content.row();
        content.add(accountInfo).left();
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
}
