package gameEngine.audio;

import javafx.scene.media.AudioClip;

public class Sound {

    private AudioClip clip;
    private float volume;

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

    public void play() {
        if (clip != null) {
            clip.play();
        }
    }

    public void pause() {
        if (clip != null) {
            clip.stop();
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (clip != null) {
            clip.setVolume(volume);
        }
    }
}
