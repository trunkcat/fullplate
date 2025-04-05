package settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameSettings {

    private float musicVolume;
    private float soundVolume;
    private boolean mute;
    private String username;

    private Preferences preferences = Gdx.app.getPreferences("User Preferences");

    public GameSettings() {
        this.musicVolume = 0.5f;
        this.soundVolume = 0.5f;
        this.mute = true;
    }

    public void loadSettings() {
        musicVolume = preferences.getFloat("musicVolume", 0.5f);
        soundVolume = preferences.getFloat("soundVolume", 0.5f);
        mute = preferences.getBoolean("mute", false);
    }

    public void saveSettings() {
        preferences.putFloat("musicVolume", musicVolume);
        preferences.putFloat("soundVolume", soundVolume);
        preferences.putBoolean("mute", mute);
        preferences.flush();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
        if (mute) {
            musicVolume = 0;
            soundVolume = 0;
        } else {
            musicVolume = preferences.getFloat("musicVolume");
            soundVolume = preferences.getFloat("soundVolume");
        }
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

    public void setPreferences(com.badlogic.gdx.Preferences preferences) {
        this.preferences = preferences;
    }
}
