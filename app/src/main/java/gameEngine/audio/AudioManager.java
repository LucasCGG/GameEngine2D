package gameEngine.audio;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static AudioManager instance;

    private Map<String, Sound> sounds = new HashMap<>();
    private Map<String, Music> music = new HashMap<>();
    private Music currentMusic = null;

    private float masterVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private float musicVolume = 0.5f;

    private AudioManager() {
    }

    public static AudioManager get() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadSound(String name, String path) {
        sounds.put(name, new Sound(path, sfxVolume * masterVolume));
    }

    public void loadMusic(String name, String path) {
        music.put(name, new Music(path, musicVolume * masterVolume));
    }

    public void playSound(String name) {
        Sound s = sounds.get(name);
        if (s != null) {
            s.play();
        } else {
            System.err.println("AudioManager: unknown sound '" + name + "'");
        }
    }

    public void playMusic(String name) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = music.get(name);
        if (currentMusic != null) {
            currentMusic.play();
        } else {
            System.err.println("AudioManager: unknown music '" + name + "'");
        }
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public void setMasterVolume(float volume) {
        masterVolume = volume;
        sounds.values().forEach(sound -> sound.setVolume(sfxVolume * masterVolume));
        music.values().forEach(m -> m.setVolume(musicVolume * masterVolume));
    }

    public void setSfxVolume(float volume) {
        sfxVolume = volume;
        sounds.values().forEach(sound -> sound.setVolume(sfxVolume * masterVolume));
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        music.values().forEach(m -> m.setVolume(musicVolume * masterVolume));
    }
}
