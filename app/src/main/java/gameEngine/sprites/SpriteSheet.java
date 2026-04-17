package gameEngine.sprites;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class SpriteSheet {

    private Image image;
    private int frameWidth, frameHeight;
    private final String sourcePath;

    public SpriteSheet(String path, int frameWidth, int frameHeight) {
        this.sourcePath = path;
        this.image = AssetPool.getImage(path);
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public WritableImage getFrame(int index) {
        if (image == null)
            return null;

        int cols = (int) (image.getWidth() / frameWidth);
        int rows = (int) (image.getHeight() / frameHeight);
        int totalFrames = cols * rows;

        System.out.println("Sprite Sheet Frames: " + totalFrames);

        if (index < 0 || index >= totalFrames) {
            System.err.println("SpriteSheet: frame index " + index + " out of bounds (total: " + totalFrames + ")");
            return null;
        }

        int col = index % cols;
        int row = index / cols;
        int x = col * frameWidth;
        int y = row * frameHeight;
        int w = Math.min(frameWidth, (int) image.getWidth() - x);
        int h = Math.min(frameHeight, (int) image.getHeight() - y);

        if (w <= 0 || h <= 0)
            return null;

        return new WritableImage(image.getPixelReader(), x, y, w, h);
    }

    public int getTotalFrames() {
        if (image == null)
            return 0;
        int cols = (int) (image.getWidth() / frameWidth);
        int rows = (int) (image.getHeight() / frameHeight);
        return cols * rows;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }
}