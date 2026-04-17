package gameEngine.sprites;

import javafx.scene.image.WritableImage;

public class Animation {

    private String name;
    private WritableImage[] cachedFrames;
    private float frameDuration;
    private boolean loop;

    private int currentFrameIndex = 0;
    private float timer = 0;
    private boolean finished = false;

    /**
     * Creates an animation from a subset of frames in a sprite sheet.
     *
     * @param name unique name used to reference this animation in an Animator
     * @param sheet the sprite sheet to extract frames from
     * @param frameIndices ordered array of frame indices to play
     * @param frameDuration time each frame is displayed, in seconds
     * @param loop if {@code true} the animation repeats; otherwise it stops on
     * the last frame
     */
    public Animation(String name, SpriteSheet sheet, int[] frameIndices, float frameDuration, boolean loop) {
        this.name = name;
        this.frameDuration = frameDuration;
        this.loop = loop;

        this.cachedFrames = new WritableImage[frameIndices.length];
        for (int i = 0; i < frameIndices.length; i++) {
            cachedFrames[i] = sheet.getFrame(frameIndices[i]);
        }
    }

    /**
     * Advances the animation timer and moves to the next frame when due. Does
     * nothing if the animation has finished and is not looping.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     */
    public void update(float deltaTime) {
        if (finished) {
            return;
        }
        timer += deltaTime;
        if (timer >= frameDuration) {
            timer -= frameDuration;
            currentFrameIndex++;
            if (currentFrameIndex >= cachedFrames.length) {
                if (loop) {
                    currentFrameIndex = 0;
                } else {
                    currentFrameIndex = cachedFrames.length - 1;
                    finished = true;
                }
            }
        }
    }

    /**
     * Returns the frame image to display this tick.
     *
     * @return the current frame as a WritableImage
     */
    public WritableImage getCurrentFrame() {
        return cachedFrames[currentFrameIndex];
    }

    /**
     * Resets the animation to its first frame.
     */
    public void reset() {
        currentFrameIndex = 0;
        timer = 0;
        finished = false;
    }

    /**
     * Returns whether the animation has reached its final frame and is not set
     * to loop.
     *
     * @return {@code true} if the animation is finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Returns the name of this animation.
     *
     * @return the animation name
     */
    public String getName() {
        return name;
    }
}
