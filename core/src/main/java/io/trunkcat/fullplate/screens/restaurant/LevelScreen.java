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
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.trunkcat.fullplate.components.BurgerBunTray;
import io.trunkcat.fullplate.components.BurgerPattyTray;
import io.trunkcat.fullplate.components.CuttingBoard;
import io.trunkcat.fullplate.components.FryingPan;
import io.trunkcat.fullplate.components.Plate;
import io.trunkcat.fullplate.components.TomatoBowl;
import io.trunkcat.fullplate.components.base.CustomerManager;
import io.trunkcat.fullplate.components.base.FoodCombinationsManager;
import io.trunkcat.fullplate.components.base.Item;
import io.trunkcat.fullplate.components.base.LevelEvent;
import io.trunkcat.fullplate.screens.BaseScreen;
import io.trunkcat.fullplate.screens.CustomStage;
import io.trunkcat.fullplate.screens.ScreenID;

// TODO: Extract hud and kitchen
public class LevelScreen extends BaseScreen {
    private final Stage hudStage;
    private final CustomStage levelStage;
    private final LevelData levelData;
    private final LevelProgress levelProgress;

    // HUD elements
    private Label experiencePointsLabel;
    private Label coinsLabel;

    public static class LevelData {
        private final int level;
        private final int coinsGoal;

        // TODO: temporary constructor -> remove after HTTP response implemented
        public LevelData(int level, int coinsGoal) {
            this.level = level;
            this.coinsGoal = coinsGoal;
        }

        public int getCoinsGoal() {
            return coinsGoal;
        }

        public int getLevel() {
            return level;
        }
    }

    public static class LevelProgress {
        private int maximumPossibleCoins;
        private int maximumPossibleTips;
        private int coins = 0;
        private int tip = 0;
        private int experiencePoints = 0;

        private int customersServed = 0;
        private int customersFailed = 0;
        private int ordersServed = 0;
        private int ordersFailed = 0;

        public int getCoins() {
            return coins;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }

        public int getTip() {
            return tip;
        }

        public void setTip(int tips) {
            this.tip = tips;
        }

        public int getExperiencePoints() {
            return experiencePoints;
        }

        public void setExperiencePoints(int experiencePoints) {
            this.experiencePoints = experiencePoints;
        }

        public int getCustomersServed() {
            return customersServed;
        }

        public void setCustomersServed(int customersServed) {
            this.customersServed = customersServed;
        }

        public int getCustomersFailed() {
            return customersFailed;
        }

        public void setCustomersFailed(int customersFailed) {
            this.customersFailed = customersFailed;
        }

        public int getOrdersServed() {
            return ordersServed;
        }

        public void setOrdersServed(int ordersServed) {
            this.ordersServed = ordersServed;
        }

        public int getOrdersFailed() {
            return ordersFailed;
        }

        public void setOrdersFailed(int ordersFailed) {
            this.ordersFailed = ordersFailed;
        }

        public int getMaximumPossibleCoins() {
            return maximumPossibleCoins;
        }

        public void setMaximumPossibleCoins(int maximumPossibleCoins) {
            this.maximumPossibleCoins = maximumPossibleCoins;
        }

        public int getMaximumPossibleTips() {
            return maximumPossibleTips;
        }

        public void setMaximumPossibleTips(int maximumPossibleTips) {
            this.maximumPossibleTips = maximumPossibleTips;
        }
    }

    public LevelScreen() {
        super(ScreenID.LEVEL_SCREEN);

        // Make level data passed on from the constructor parameters.
        levelData = new LevelData(2, 1);

        hudStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        levelStage = new CustomStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
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
                        LevelEvent.LevelCompletedEvent e = (LevelEvent.LevelCompletedEvent) event;
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

        setupHUD();
        setupKitchen();
    }

    @Override
    public void show() {
        Gdx.app.log("Stage", "Viewport world size: " + levelStage.getViewport().getWorldWidth() +
            "x" + levelStage.getViewport().getWorldHeight() +
            ", screen size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
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
        hudStage.dispose();
        levelStage.dispose();
    }

    // FIXME: shouldn't be this. abstract or extract.
    private String getCoinsText() {
        return levelProgress.getCoins() + " / " + levelData.getCoinsGoal() + " coins";
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
        BurgerPattyTray burgerPattyTray = new BurgerPattyTray(1, 5);
        addLevelActor(burgerPattyTray, 100, 100);

        BurgerBunTray burgerBunTray = new BurgerBunTray(1, 5);
        addLevelActor(burgerBunTray, 300, 100);

        Plate plate1 = new Plate(1);
        addLevelActor(plate1, 500, 100);

        FryingPan fryingPan1 = new FryingPan(1);
        addLevelActor(fryingPan1, 700, 100);

        TomatoBowl tomatoBowl = new TomatoBowl(1, 500);
        addLevelActor(tomatoBowl, 900, 300);

        CuttingBoard cuttingBoard1 = new CuttingBoard(1);
        addLevelActor(cuttingBoard1, 900, 100);

        CustomerManager customerManager = new CustomerManager(
            new Vector2[]{
                // TODO: calculate this based on world width, customer actor width, and y pos.
                new Vector2(100, 700),
                new Vector2(400, 700),
                new Vector2(700, 700),
                new Vector2(1000, 700),
            },
            FoodCombinationsManager.from(
                Plate.COMBINATION_MANAGER
            ),
            levelData,
            levelProgress
        );
        levelStage.addActor(customerManager);
    }

    private void addLevelActor(Item item, int x, int y) {
        item.setPosition(x, y);
        levelStage.addActor(item);
    }
}
