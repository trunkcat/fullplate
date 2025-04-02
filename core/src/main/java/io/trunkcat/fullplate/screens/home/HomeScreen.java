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
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Arrays;
import java.util.Comparator;

import io.trunkcat.fullplate.CookGame;
import io.trunkcat.fullplate.entities.PlaceData;
import io.trunkcat.fullplate.models.PlayerData;
import io.trunkcat.fullplate.models.responses.PlayerStats;


public class HomeScreen implements com.badlogic.gdx.Screen {
    private final CookGame game;

    private final Stage hudStage;
    private final Stage mapStage;
    private final MapGestureListener mapGestureHandler;

    PlayerData[] playerData;


    public HomeScreen() {
        game = CookGame.getInstance();

        hudStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setupHUD();

        OrthographicCamera mapCamera = new OrthographicCamera();
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
        mapGestureHandler = new MapGestureListener(mapCamera, mapImage.getWidth(), mapImage.getHeight());

        PlaceData[] samplePlaces = new PlaceData[]{
            new PlaceData(
                "burger-place",
                PlaceData.PlaceType.RESTAURANT,
                "Burger Place",
                "A place for burgers",
                null,
                0,
                5,
                false,
                new Vector2(2000, 400)),
            new PlaceData(
                "noodle-stand",
                PlaceData.PlaceType.RESTAURANT,
                "Noodle stand",
                "Craving for noodles? You got it!",
                1000,
                1,
                5,
                true,
                new Vector2(1500, 700)),
            new PlaceData(
                "pizza-place",
                PlaceData.PlaceType.RESTAURANT,
                "Pizza Place",
                "Dem Pizzas!!!",
                2000,
                15,
                40,
                true,
                new Vector2(1750, 1000))};

        setupMapPlaces(samplePlaces);

        playerData = new PlayerData[]{
            new PlayerData(
                1,
                "Sreeerag",
                new PlayerStats(
                    64,
                    1000,
                    15)
            ),
            new PlayerData(
                2,
                "Sidharth",
                new PlayerStats(
                    63,
                    750,
                    15
                )
            ),
            new PlayerData(
                3,
                "Shivani",
                new PlayerStats(
                    62,
                    3000,
                    15)
            ),
            new PlayerData(
                4,
                "Sharun",
                new PlayerStats(
                    61,
                    200,
                    15
                )
            ),
            new PlayerData(
                5,
                "Shahina",
                new PlayerStats(
                    59,
                    500,
                    15)
            ),
            new PlayerData(
                6,
                "You",
                new PlayerStats(
                    15,
                    1200,
                    15
                )
            ),
            game.player.data,
        };
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
        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showLeaderboardWindow();
                ;
            }
        });
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
        for (Cell<?> children : bottomBar.getCells()) {
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

    class PlacePointer extends Button {
        private PlaceData placeData;

        public PlacePointer(PlaceData placeData) {
            this.placeData = placeData;
            TextButton closeButton = new TextButton("Close", game.skin);

            if (placeData.isLocked()) {
                this.setStyle(game.skin.get("place-lock-button", ButtonStyle.class));
            } else {
                this.setStyle(game.skin.get("place-play-button", ButtonStyle.class));
            }

            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Window window = createWindow();
                    Table content = new Table();
                    content.defaults().pad(10).fillX();

                    Label heading = new Label(placeData.getName(), game.skin, "h1");
                    content.add(heading).expandX().left();
                    content.row();

                    closeButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            window.remove();
                        }
                    });

                    TextButton buyButton = new TextButton("Buy", game.skin);
                    buyButton.addListener(new ChangeListener() {
                        private boolean errorLabelAdded = false;
                        boolean unlockable = game.player.data.getStats().getPlayerLevel() >= placeData.getRequiredLevel();

                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (unlockable) {
                                int playerMoney = game.player.data.getStats().getCoins();
                                if (playerMoney >= placeData.getCost()) {
                                    placeData.setLocked(false);
                                    setStyle(game.skin.get("place-play-button", ButtonStyle.class));
                                    game.player.data.getStats().setCoins(playerMoney - placeData.getCost());
                                    hudStage.clear();
                                    setupHUD();
                                    window.remove();
                                } else {
                                    if (!errorLabelAdded) {
                                        Label neededMoney = new Label("You need $" + (int) (placeData.getCost() - playerMoney) + "!", game.skin, "h1");
                                        content.add(neededMoney).expandX().uniform();
                                        content.row();
                                        errorLabelAdded = true;
                                    }
                                }
                            } else {
                                if (!errorLabelAdded) {
                                    Label lockState = new Label("You do not meet the levels to unlock this place!", game.skin, "h2");
                                    lockState.setWrap(true);
                                    content.add(lockState).expandX().fillX().pad(10).uniform();
                                    content.row();
                                    errorLabelAdded = true;
                                }

                            }
                        }
                    });

                    TextButton playButton = getTextButton(heading);

                    if (placeData.getCost() != null && placeData.isLocked()) {
                        Label info = new Label(placeData.getDescription(), game.skin, "h2");
                        content.add(info).expandX().left();
                        content.row();
                        Label cost = new Label("$ " + placeData.getCost().toString(), game.skin, "h1");
                        content.add(cost).expandX().center();
                        content.row();
                        content.add(closeButton).expandX().left().uniform();
                        content.add(buyButton).fillX().uniform();
                        content.row();
                    } else {
                        //TODO : Get Play button window working
                        Label description = new Label(placeData.getDescription(), game.skin, "h2");
                        content.add(description).expandX().left();
                        content.row();
                        content.add(closeButton).expandX().left().uniform();
                        content.add(playButton).fillX().uniform();
                        content.row();
                    }


                    setWindowContent(window, content);
                    hudStage.addActor(window);
                }

                private TextButton getTextButton(Label heading) {
                    TextButton playButton = new TextButton("Play", game.skin);

                    playButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            Window levelWindow = createWindow();
                            Table levelContent = new Table();
                            levelContent.defaults().pad(10).fillX();

                            closeButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    levelWindow.remove();
                                }
                            });

                            Table levelsTable = new Table();
                            levelsTable.defaults().pad(10).fillX();

                            // Add levels to the table
                            ButtonGroup<Button> buttonGroup = new ButtonGroup<>();
                            buttonGroup.setMaxCheckCount(1);
                            buttonGroup.setMinCheckCount(1);

                            int levelCount = placeData.getLevelCount();
                            for (int i = 1; i <= levelCount; i++) {
                                TextButton button = new TextButton(String.valueOf(i), game.skin);
                                buttonGroup.add(button);
                                levelsTable.add(button).expandX().fillX();
                                if (i % 3 == 0) levelsTable.row(); // New row every 3 buttons
                            }

                            levelsTable.row();

                            ScrollPane scrollPane = new ScrollPane(levelsTable, game.skin);
                            scrollPane.setScrollingDisabled(true, false);
                            scrollPane.setFadeScrollBars(true);

                            levelContent.add(heading).expandX().left();
                            levelContent.row();
                            levelContent.add(scrollPane).expand().fill().height(300);
                            levelContent.row();
                            levelContent.add(closeButton).expandX().uniform();
                            levelContent.row();

                            setWindowContent(levelWindow, levelContent);
                            hudStage.addActor(levelWindow);
                        }
                    });
                    return playButton;
                }
            });

            setBounds(placeData.getPosition().x, placeData.getPosition().y, 100, 100);
            setOrigin(Align.center);
        }
    }

    private void setupMapPlaces(PlaceData[] places) {
        Group placesGroup = new Group();

        for (PlaceData placeData : places) {
            placesGroup.addActor(new PlacePointer(placeData));
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
        window.setPosition(Gdx.graphics.getWidth() / 2f - windowWidth / 2f, Gdx.graphics.getHeight() / 2f - windowHeight / 2f);
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

    private void showLeaderboardWindow() {
        // Sort by coins in descending order
        Arrays.sort(playerData, Comparator.comparingInt(p -> -p.getStats().getPlayerLevel()));

        // Create Window
        Window leaderboard = createWindow();
        Table content = new Table();
        content.defaults().pad(10).fillX();

        content.add(new Label("Leaderboard", game.skin, "h1")).left().row();

//        addLeaderboardHeader(content);

        Table leaderboardTable = new Table();
        leaderboardTable.defaults().pad(10).fillX();

        boolean userInLeaderboard = false;

        for (int i = 0; i < Math.min(5, playerData.length); i++) {
            leaderboardTable.add(createPlayerRow(i + 1, playerData[i])).fillX().row();
            if (playerData[i].getId() == game.player.data.getId()) userInLeaderboard = true;
        }

        if (!userInLeaderboard) {
            leaderboardTable.add(createPlayerRow(getPlayerRank(), game.player.data)).fillX().row();
        }

        ScrollPane scrollPane = new ScrollPane(leaderboardTable, game.skin);
        scrollPane.setFadeScrollBars(false);
        content.add(scrollPane).expandX().row();

        TextButton closeButton = new TextButton("Close", game.skin);
        closeButton.addListener(event -> {
            leaderboard.remove();
            return true;
        });

        content.add(closeButton).uniform().center().padTop(20);

        setWindowContent(leaderboard, content);
        hudStage.addActor(leaderboard);
    }

    private void addLeaderboardHeader(Table table) {
        Label rankLabel = new Label("#", game.skin, "h1");
        Label nameLabel = new Label("nickname", game.skin, "h1");
        Label levelLabel = new Label("player level", game.skin, "h1");

        table.add(rankLabel).left();
        table.add(nameLabel).center();
        table.add(levelLabel).right();
        table.row();
    }

    private Table createPlayerRow(int rank, PlayerData player) {
        Table row = new Table();
        row.defaults().pad(10).fillX();

        Label rankLabel = new Label(String.valueOf(rank), game.skin);
        Label nameLabel = new Label(player.getUsername(), game.skin);
        Label levelLabel = new Label("LVL " + player.getStats().getPlayerLevel(), game.skin);

        row.add(rankLabel).width(50).left();
        row.add(nameLabel).expandX().left();
        row.add(levelLabel).width(100).right();

        return row;
    }

    private int getPlayerRank() {
        for (int i = 0; i < playerData.length; i++) {
            if (playerData[i].getId() == game.player.data.getId()) {
                return i + 1;
            }
        }
        return playerData.length;
    }
}
