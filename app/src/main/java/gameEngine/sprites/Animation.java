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

    public Animation(String name, SpriteSheet sheet, int[] frameIndices, float frameDuration, boolean loop) {
        this.name = name;
        this.frameDuration = frameDuration;
        this.loop = loop;

        this.cachedFrames = new WritableImage[frameIndices.length];
        for (int i = 0; i < frameIndices.length; i++) {
            cachedFrames[i] = sheet.getFrame(frameIndices[i]);
        }
    }

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

    public WritableImage getCurrentFrame() {
        return cachedFrames[currentFrameIndex];
    }

    public void reset() {
        currentFrameIndex = 0;
        timer = 0;
        finished = false;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getName() {
        return name;
    }
}
