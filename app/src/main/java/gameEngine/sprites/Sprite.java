package gameEngine.sprites;

import gameEngine.core.GameObject;
import gameEngine.core.Transform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Sprite extends GameObject {

    private Image staticImage; // use if static image
    private Animator animator; // use if animated

    /**
     * Creates a static (non -animated) sprite using an image loaded from the
     * asset pool.
     *
     * @param name display name for this game object
     * @param imagePath resource path to the image, relative to the classpath
     * root
     * @param transform initial position , size, and rotation
     *
     */
    public Sprite(String name, String imagePath, Transform transform) {
        super(name, transform);
        this.staticImage = AssetPool.getImage(imagePath);
    }

    /**
     * Creates an animated sprite driven by an Animator.
     *
     * @param name display name for this game object
     * @param animator the animator controlling which animation plays
     * @param transform initial position, size, and rotation
     */
    public Sprite(String name, Animator animator, Transform transform) {
        super(name, transform);
        this.animator = animator;
    }

    @Override
    public void update(float deltaTime) {
        if (animator != null) {
            animator.update(deltaTime);
        }
    }

    @Override
    public void draw(GraphicsContext graphicsCtx) {
        WritableImage frame = animator != null ? animator.getCurrentFrame() : null;
        Image toDraw = frame != null ? frame : staticImage;

        if (toDraw != null) {
            graphicsCtx.drawImage(toDraw, 0, 0, transform.width, transform.height);
        }
    }

    /**
     * Returns the animator attached to this sprite, or {@code null} if this
     * sprite is static.
     *
     * @return the Animator, or {@code null}
     */
    public Animator getAnimator() {
        return animator;
    }
}
