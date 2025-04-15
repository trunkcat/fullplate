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
import io.trunkcat.fullplate.entities.Session;
import io.trunkcat.fullplate.models.GameData;
import io.trunkcat.fullplate.models.responses.PlayerData;
import io.trunkcat.fullplate.network.HTTPClient;
import io.trunkcat.fullplate.screens.BaseScreen;
import io.trunkcat.fullplate.screens.LoadingScreen;
import io.trunkcat.fullplate.screens.LoginScreen;
import io.trunkcat.fullplate.screens.ScreenID;
import io.trunkcat.fullplate.utilities.AudioManager;
import io.trunkcat.fullplate.utilities.Constants;
import io.trunkcat.fullplate.utilities.GameFont;

public class CookGame extends Game {
	public Viewport viewport;
	public GameFont PallyFont;
	public HTTPClient httpClient;
	public Session session;
	public PlayerData player;
	public GameData data;
	public Preferences preferences;
	public AudioManager audioManager;
	private ScreenID currentScreen = ScreenID.UNKNOWN;
	public Debug debug;

	public Skin skin;
	public Skin testSkin;

	@Override
	public void create() {
		viewport = new ScreenViewport();

		httpClient = new HTTPClient("http://192.168.29.36:8080/api");
		preferences = Gdx.app.getPreferences("Full plate Preferences");

		skin = new Skin(Gdx.files.internal("cook-skin/0.5/skin.json"));
		testSkin = new Skin(Gdx.files.internal("test-skin/skin.json"));
		PallyFont = new GameFont("fonts/Pally-Regular.otf");

		audioManager = new AudioManager();
		debug = new Debug();

		session = new Session();
		player = new PlayerData();
		data = new GameData();

		String sessionToken = preferences.getString(Constants.PREF_KEY_SESSION_TOKEN);

		if (sessionToken != null && !sessionToken.isEmpty()) {
			httpClient.setAuthSessionToken(sessionToken);
			setScreen(new LoadingScreen());
		} else {
			setScreen(new LoginScreen());
		}
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
