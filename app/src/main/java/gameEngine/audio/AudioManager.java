package gameEngine.audio;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static AudioManager INSTANCE = new AudioManager();

    private Map<String, Sound> sounds = new HashMap<>();
    private Map<String, Music> music = new HashMap<>();
    private Music currentMusic = null;

    private float masterVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private float musicVolume = 0.5f;

    private AudioManager() {
    }

    /**
     * Returns the singleton AudioManager instance, creating it if necessary.
     *
     * @return the global AudioManager instance
     */
    public static AudioManager get() {
        if (INSTANCE == null) {
            INSTANCE = new AudioManager();
        }
        return INSTANCE;
    }

    /**
     * Loads a sound effect and registers it under the given name.
     *
     * @param name unique key used to play this sound later
     * @param path resource path to the audio file
     */
    public void loadSound(String name, String path) {
        sounds.put(name, new Sound(path, sfxVolume * masterVolume));
    }

    /**
     * Loads a music track and registers it under the given name.
     *
     * @param name unique key used to play this track later
     * @param path resource path to the audio file
     */
    public void loadMusic(String name, String path) {
        music.put(name, new Music(path, musicVolume * masterVolume));
    }

    /**
     * Plays the sound effect registered under the given name. Logs an error if
     * the name is not found.
     *
     * @param name the key of the sound to play
     */
    public void playSound(String name) {
        Sound s = sounds.get(name);
        if (s != null) {
            s.play();
        } else {
            System.err.println("AudioManager: unknown sound '" + name + "'");
        }
    }

    /**
     * Stops any currently playing music and starts the track registered under
     * the given name. Logs an error if the name is not found.
     *
     * @param name the key of the music track to play
     */
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

    /**
     * Pauses the currently playing music track.
     */
    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    /**
     * Stops the currently playing music track.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    /**
     * Resumes the currently paused music track.
     */
    public void resumeMusic() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    /**
     * Sets the master volume and re-applies it to all loaded sounds and music.
     *
     * @param volume volume level in the range [0.0, 1.0]
     */
    public void setMasterVolume(float volume) {
        masterVolume = volume;
        sounds.values().forEach(sound -> sound.setVolume(sfxVolume * masterVolume));
        music.values().forEach(m -> m.setVolume(musicVolume * masterVolume));
    }

    /**
     * Sets the sound effects volume and re-applies it to all loaded sounds.
     *
     * @param volume volume level in the range [0.0, 1.0]
     */
    public void setSfxVolume(float volume) {
        sfxVolume = volume;
        sounds.values().forEach(sound -> sound.setVolume(sfxVolume * masterVolume));
    }

    /**
     * Sets the music volume and re-applies it to all loaded music tracks.
     *
     * @param volume volume level in the range [0.0, 1.0]
     */
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        music.values().forEach(m -> m.setVolume(musicVolume * masterVolume));
    }
}
