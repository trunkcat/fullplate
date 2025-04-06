package io.trunkcat.fullplate.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;

import io.trunkcat.fullplate.CookGame;
import io.trunkcat.fullplate.settings.GameSettings;

public class AudioManager {
    private CookGame game;
    private GameSettings gameSettings;
    private static AudioManager instance;
    private Music currentMusic;
    private float musicVolume;
    private float soundVolume;
    private boolean isMusicMuted;
    private boolean isSoundMuted;
    private Preferences preferences;
    private final HashMap<String, Sound> soundCache = new HashMap<>();

    private AudioManager() {
        preferences = Gdx.app.getPreferences("Full plate Preferences");
        loadSoundSettings();
    }

    private void loadSoundSettings() {
        musicVolume = preferences.getFloat("musicVolume", 1.0f);
        soundVolume = preferences.getFloat("soundVolume", 1.0f);
        isMusicMuted = preferences.getBoolean("muted", false);
        isSoundMuted = preferences.getBoolean("muted", false);
    }

    private void saveSoundSettings() {
        gameSettings.saveSettings(musicVolume, soundVolume, isMusicMuted);
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // Music Controls
    public void playMusic(String filePath, boolean looping) {
        stopMusic();
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        currentMusic.setLooping(looping);
        currentMusic.setVolume(isMusicMuted ? 0f : musicVolume);
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
    }

    public void pauseMusic() {
        if (currentMusic != null) currentMusic.pause();
    }

    public void resumeMusic() {
        if (currentMusic != null) currentMusic.play();
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (currentMusic != null && !isMusicMuted) {
            currentMusic.setVolume(volume);
        }
        saveSoundSettings();
    }

    public void muteMusic(boolean mute) {
        isMusicMuted = mute;
        if (currentMusic != null) {
            currentMusic.setVolume(mute ? 0f : musicVolume);
        }
        saveSoundSettings();
    }

    public boolean isMusicMuted() {
        return isMusicMuted;
    }

    // SOUND METHODS
    public void playSound(String filePath) {
        if (isSoundMuted) return;

        Sound sound = soundCache.get(filePath);
        if (sound == null) {
            FileHandle fileHandle = Gdx.files.internal(filePath);
            sound = Gdx.audio.newSound(fileHandle);
            soundCache.put(filePath, sound);
        }

        sound.play(soundVolume);
    }

    public void setSoundVolume(float volume) {
        soundVolume = volume;
        saveSoundSettings();
    }

    public void muteSound(boolean mute) {
        isSoundMuted = mute;
        saveSoundSettings();
    }

    public boolean isSoundMuted() {
        return isSoundMuted;
    }

    public void dispose() {
        stopMusic();
        for (Sound sound : soundCache.values()) {
            sound.dispose();
        }
        soundCache.clear();
    }
}
