package io.trunkcat.fullplate.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameSettings {
    private String username;

    Preferences preferences = Gdx.app.getPreferences("Full plate Preferences");


    public GameSettings() {
    }

    public void loadSettings() {
    }

    //Called directly when settings is changed
    public void saveSettings(float musicVolume,
                             float soundVolume,
                             boolean mute) {
        preferences.putFloat("musicVolume", musicVolume);
        preferences.putFloat("soundVolume", soundVolume);
        preferences.putBoolean("mute", mute);
        preferences.flush();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Preferences getPreferences() {
        return preferences;
    }
}
