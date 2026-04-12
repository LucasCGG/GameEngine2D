package gameEngine.sprites;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.WritableImage;

public class Animator {

    private Map<String, Animation> animations = new HashMap<>();
    private Animation currentAnimation = null;
    private String currentName = null;

    public void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }

    public void play(String name) {
        if (name.equals(currentName)) {
            return;
        }

        Animation next = animations.get(name);
        if (next == null) {
            System.err.println("Animator: unknown animation '" + name + "'");
            return;
        }

        next.reset();
        currentAnimation = next;
        currentName = next.getName();
    }

    public void update(float deltaTime) {
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
        }
    }

    public WritableImage getCurrentFrame() {
        if (currentAnimation == null) {
            return null;
        }
        return currentAnimation.getCurrentFrame();
    }

    public boolean isFinished() {
        return currentAnimation != null && currentAnimation.isFinished();
    }

    public String getCurrentName() {
        return currentName;
    }
}
