package gameEngine.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Music {

    private MediaPlayer mediaPlayer;
    private float volume;

    /**
     * Loads a music track from the classpath and prepares it for looped
     * playback.
     *
     * @param path resource path to the audio file
     * @param volume initial volume in the range [0.0, 1.0]
     */
    public Music(String path, float volume) {
        this.volume = volume;

        try {
            String url = Music.class.getResource("/" + path).toExternalForm();
            Media media = new Media(url);
            this.mediaPlayer = new MediaPlayer(media);
            this.mediaPlayer.setVolume(volume);
            this.mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });
        } catch (Exception e) {
            System.err.println("Music: failed to load '" + path + "' : " + e.getMessage());
        }
    }

    /**
     * Starts or resumes playback.
     */
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    /**
     * Pauses playback at the current position.
     */
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Stops playback and rewinds to the beginning.
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Sets the playback volume.
     *
     * @param volume volume level in the range [0.0, 1.0]
     */
    public void setVolume(float volume) {
        this.volume = volume;

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    /**
     * Returns the current volume level.
     *
     * @return volume in the range [0.0, 1.0]
     */
    public float getVolume() {
        return volume;
    }
}
