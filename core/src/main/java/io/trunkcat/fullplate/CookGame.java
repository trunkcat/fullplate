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

package io.trunkcat.fullplate;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.trunkcat.fullplate.components.debug.Debug;
import io.trunkcat.fullplate.entities.Player;
import io.trunkcat.fullplate.models.PlayerData;
import io.trunkcat.fullplate.models.responses.PlayerStats;
import io.trunkcat.fullplate.network.HTTPClient;
import io.trunkcat.fullplate.screens.home.HomeScreen;
import io.trunkcat.fullplate.utilities.AudioManager;
import io.trunkcat.fullplate.screens.BaseScreen;
import io.trunkcat.fullplate.screens.ScreenID;
import io.trunkcat.fullplate.screens.restaurant.LevelScreen;
import io.trunkcat.fullplate.utilities.Constants;
import io.trunkcat.fullplate.utilities.GameFont;

public class CookGame extends Game {
    public Viewport viewport;
    public GameFont PallyFont;
    public HTTPClient httpClient;
    public Player player;
    public Preferences preferences;
    public AudioManager audioManager;
    private ScreenID currentScreen = ScreenID.UNKNOWN;
    public Debug debug;

    public Skin skin;
    public Skin testSkin;

    @Override
    public void create() {
        viewport = new ScreenViewport();
        PallyFont = new GameFont("fonts/Pally-Regular.otf");
        httpClient = new HTTPClient("http://192.168.29.36:8080/api");
        player = new Player();
        preferences = Gdx.app.getPreferences("Full plate Preferences");
        skin = new Skin(Gdx.files.internal("cook-skin/0.5/skin.json"));
        audioManager = new AudioManager();
        testSkin = new Skin(Gdx.files.internal("test-skin/skin.json"));
        debug = new Debug();

        PlayerStats stats = new PlayerStats(60, 2000, 2000);
        player.data = new PlayerData(12, "swassy", stats);
        setScreen(new HomeScreen());

//        String sessionToken = preferences.getString(Constants.PREF_KEY_SESSION_TOKEN);
//        if (sessionToken != null && !sessionToken.isEmpty()) {
//            httpClient.setAuthSessionToken(sessionToken);
//            setScreen(new LoadingScreen());
//        } else {
//            setScreen(new LoginScreen());
//        }
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);

        if (screen instanceof BaseScreen) {
            currentScreen = ((BaseScreen) screen).getScreenID();
        } else {
            currentScreen = ScreenID.UNKNOWN;
        }
    }

    public ScreenID getCurrentScreen() {
        return currentScreen;
    }

    public static CookGame getInstance() {
        return (CookGame) Gdx.app.getApplicationListener();
    }

    @Override
    public void dispose() {
        super.dispose();
        debug.dispose();
    }
}
