package gameEngine.sprites;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class SpriteSheet {

    private Image image;
    private int frameWidth, frameHeight;
    private final String sourcePath;

    /**
     * Loads a sprite sheet from the asset pool and prepares it for frame
     * extraction.
     *
     * @param path resource path to the image, relative to the classpath root
     * @param frameWidth width of a single frame in pixels
     * @param frameHeight height of a single frame in pixels
     */
    public SpriteSheet(String path, int frameWidth, int frameHeight) {
        this.sourcePath = path;
        this.image = AssetPool.getImage(path);
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    /**
     * Extracts a single frame from the sheet by its linear index
     * (left-to-right, top-to-bottom).
     *
     * @param index zero-based frame index
     * @return the frame as a WritableImage, or {@code null} if the index is out
     * of bounds or the source image failed to load
     */
    public WritableImage getFrame(int index) {
        if (image == null) {
            return null;
        }

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

        if (w <= 0 || h <= 0) {
            return null;
        }

        return new WritableImage(image.getPixelReader(), x, y, w, h);
    }

    /**
     * Returns the total number of frames in the sheet based on the current
     * frame dimensions.
     *
     * @return total frame count, or 0 if the image failed to load
     */
    public int getTotalFrames() {
        if (image == null) {
            return 0;
        }
        int cols = (int) (image.getWidth() / frameWidth);
        int rows = (int) (image.getHeight() / frameHeight);
        return cols * rows;
    }

    /**
     * @return the resource path this sheet was loaded from
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @return the configured frame width in pixels
     */
    public int getFrameWidth() {
        return frameWidth;
    }

    /**
     * @return the configured frame height in pixels
     */
    public int getFrameHeight() {
        return frameHeight;
    }
}
