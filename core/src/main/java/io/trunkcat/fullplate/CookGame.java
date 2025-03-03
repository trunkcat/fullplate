package io.trunkcat.fullplate;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.trunkcat.fullplate.models.PlayerData;
import io.trunkcat.fullplate.network.HTTPClient;
import io.trunkcat.fullplate.screens.LoadingScreen;
import io.trunkcat.fullplate.screens.LoginScreen;
import io.trunkcat.fullplate.utilities.GameFont;
import io.trunkcat.fullplate.utilities.GameScreen;

public class CookGame extends Game {
    public Viewport viewport;
    public GameFont SigmarFont;
    public HTTPClient httpClient;
    public PlayerData playerData;
    public Preferences preferences;

    @Override
    public void create() {
        viewport = new ScreenViewport();
        SigmarFont = new GameFont("fonts/Sigmar/Sigmar-Regular.ttf");
        httpClient = new HTTPClient("http://192.168.29.36:8080");
        playerData = new PlayerData();
        preferences = Gdx.app.getPreferences("Full plate Preferences");
        
        String sessionToken = preferences.getString("sessionToken");
        if (sessionToken != null && !sessionToken.isEmpty()) {
            httpClient.setAuthSessionToken(sessionToken);
            setScreen(new LoadingScreen(this));
        } else {
            setScreen(new LoginScreen(this));
        }
    }
}
