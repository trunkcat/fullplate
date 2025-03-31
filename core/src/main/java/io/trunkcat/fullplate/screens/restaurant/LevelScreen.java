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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.trunkcat.fullplate.CookGame;
import io.trunkcat.fullplate.components.BurgerPattyTray;
import io.trunkcat.fullplate.components.Plate;
import io.trunkcat.fullplate.components.base.BurgerBunTray;
import io.trunkcat.fullplate.components.base.Item;

public class LevelScreen implements com.badlogic.gdx.Screen {
    private final CookGame game;

    private final Stage hudStage;
    private final Stage levelStage;

    private final DragAndDrop dragAndDrop;

    public LevelScreen() {
        game = CookGame.getInstance();
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setKeepWithinStage(true);

        hudStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setupHUD();

        levelStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Viewport viewport = levelStage.getViewport();

        // TODO: make the background adaptive:
        Image backgroundImage = new Image(new Texture(Gdx.files.internal("backgrounds/restaurants/burger-place.png")));
        if (backgroundImage.getHeight() < viewport.getWorldHeight() || backgroundImage.getWidth() < viewport.getWorldWidth()) {
            float aspectRatio = backgroundImage.getWidth() / backgroundImage.getHeight();
            backgroundImage.setHeight(viewport.getWorldHeight());
            backgroundImage.setWidth(viewport.getWorldHeight() * (aspectRatio));
        }
        backgroundImage.setPosition((viewport.getWorldWidth() - backgroundImage.getWidth()) / 2f, 0);
//        levelStage.addActor(backgroundImage);

        Image tableImage = new Image(new Texture(Gdx.files.internal("restaurants/table.png")));
        float tableImageAspectRatio = tableImage.getHeight() / tableImage.getWidth();
        tableImage.setWidth(viewport.getWorldWidth());
        tableImage.setHeight(viewport.getWorldWidth() * tableImageAspectRatio);
//        levelStage.addActor(tableImage);

        setupKitchen();
    }

    @Override
    public void show() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hudStage);
        inputMultiplexer.addProcessor(levelStage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        levelStage.act(delta);
        levelStage.draw();

        hudStage.act(delta);
        hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        hudStage.getViewport().update(width, height, true);
        levelStage.getViewport().update(width, height, true);
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
        levelStage.dispose();
    }

    private void setupHUD() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // TOP BAR
        Table topBar = new Table();
        Table centerElements = new Table();
        centerElements.setBackground(game.skin.getDrawable("rect-top"));

        Label experiencePointsLabel = new Label(game.player.data.getStats().getExperiencePoints() + " xp", game.skin, "h2");
        centerElements.add(experiencePointsLabel).space(15).padRight(30);
        Label coinsLabel = new Label(game.player.data.getStats().getCoins() + " coins", game.skin, "h2");
        centerElements.add(coinsLabel).space(15);

        Button settingsButton = new Button(game.skin, "settings-button");
        centerElements.add(settingsButton).size(64, 64).right().padRight(15).pad(5);

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
        for (Cell<?> cell : table.getCells()) {
            size.x += cell.getPrefWidth();
            size.y += cell.getPrefHeight();
        }
        return size;
    }

    private void setupKitchen() {
        BurgerPattyTray burgerPattyTray = new BurgerPattyTray(1, 5);
        addLevelActor(burgerPattyTray, 100, 100);

        BurgerBunTray burgerBunTray = new BurgerBunTray(1, 5);
        addLevelActor(burgerBunTray, 300, 100);

        Plate plate1 = new Plate(1);
        addLevelActor(plate1, 500, 100);

//        Plate plate2 = new Plate(1);
//        addLevelActor(plate2, 700, 100);
    }

    private void addLevelActor(Item item, int x, int y) {
        DragAndDrop.Source dragSource = item.getDragSource();
        DragAndDrop.Target dropTarget = item.getDropTarget();
        if (dragSource != null) {
            dragAndDrop.addSource(dragSource);
        }
        if (dropTarget != null) {
            dragAndDrop.addTarget(dropTarget);
        }

        item.setPosition(x, y);
        levelStage.addActor(item);
    }
}
