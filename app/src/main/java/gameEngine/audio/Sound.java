package gameEngine.audio;

import javafx.scene.media.AudioClip;

public class Sound {

    private AudioClip clip;
    private float volume;

    /**
     * Loads a sound effect from the classpath.
     *
     * @param path resource path to the audio file
     * @param volume initial volume in the range [0.0, 1.0]
     */
    public Sound(String path, float volume) {
        this.volume = volume;

        try {
            String url = Sound.class.getResource("/" + path).toExternalForm();
            this.clip = new AudioClip(url);
            this.clip.setVolume(volume);

        } catch (Exception e) {
            System.err.println("Sound: failed to load '" + path + "': " + e.getMessage());
        }
    }

    /**
     * Plays the sound effect from the beginning.
     */
    public void play() {
        if (clip != null) {
            clip.play();
        }
    }

    /**
     * Stops playback.
     */
    public void pause() {
        if (clip != null) {
            clip.stop();
        }
    }

    /**
     * Sets the playback volume.
     *
     * @param volume volume level in the range [0.0, 1.0]
     */
    public void setVolume(float volume) {
        this.volume = volume;
        if (clip != null) {
            clip.setVolume(volume);
        }
    }
}
