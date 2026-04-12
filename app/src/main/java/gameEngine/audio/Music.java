package gameEngine.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Music {

    private MediaPlayer mediaPlayer;
    private float volume;

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

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public float getVolume(){
        return volume;
    }
}
