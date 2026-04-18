package gameEngine.sprites;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.WritableImage;

public class Animator {

    private Map<String, Animation> animations = new HashMap<>();
    private Animation currentAnimation = null;
    private String currentName = null;

    /**
     * Registers an animation so it can be played by name.
     *
     * @param animation the animation to add
     */
    public void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }

    /**
     * Switches to the named animation, resetting it from the beginning. Does
     * nothing if the named animation is already playing. Logs an error if the
     * name is not registered.
     *
     * @param name the name of the animation to play
     */
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

    /**
     * Advances the current animation by the given time step.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     */
    public WritableImage getCurrentFrame() {
        if (currentAnimation == null) {
            return null;
        }
        return currentAnimation.getCurrentFrame();
    }

    /**
     * Returns whether the current animation has reached its last frame and is
     * not set to loop.
     *
     * @return {@code true} if the current animation has finished
     */
    public boolean isFinished() {
        return currentAnimation != null && currentAnimation.isFinished();
    }

    /**
     * Returns the name of the currently playing animation.
     *
     * @return the animation name, or {@code null} if none is playing
     */
    public String getCurrentName() {
        return currentName;
    }

}
