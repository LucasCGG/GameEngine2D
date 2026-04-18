package gameEngine.sprites;

import gameEngine.core.GameObject;
import gameEngine.core.Transform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Sprite extends GameObject {

    private Image staticImage; // use if static image
    private Animator animator; // use if animated
    private boolean flipCheck = false;

    private WritableImage previewOverride = null;

    /**
     * Creates a static (non -animated) sprite using an image loaded from the
     * asset pool.
     *
     * @param name      display name for this game object
     * @param imagePath resource path to the image, relative to the classpath
     *                  root
     * @param transform initial position , size, and rotation
     * @param flipCheck if true, mirrors sprite if rotation more than specific
     *                  threshold
     *
     */
    public Sprite(String name, String imagePath, Transform transform, Boolean flipCheck) {
        super(name, transform);
        this.staticImage = AssetPool.getImage(imagePath);
        this.flipCheck = flipCheck;
    }

    /**
     * Creates an animated sprite driven by an Animator.
     *
     * @param name      display name for this game object
     * @param animator  the animator controlling which animation plays
     * @param transform initial position, size, and rotation
     * @param flipCheck if true, mirrors sprite if rotation more than specific
     *                  threshold
     * 
     */
    public Sprite(String name, Animator animator, Transform transform, Boolean flipCheck) {
        super(name, transform);
        this.animator = animator;
        this.flipCheck = flipCheck;
    }

    @Override
    public void update(float deltaTime) {
        if (animator != null) {
            animator.update(deltaTime);
        }
    }

    @Override
    public void draw(GraphicsContext graphicsCtx) {
        Image toDraw = resolveFrame();
        if (toDraw == null)
            return;

        boolean flipped = flipCheck && Math.abs(transform.rotation) > 90f;
        if (flipped) {
            graphicsCtx.save();
            graphicsCtx.translate(0, transform.height);
            graphicsCtx.scale(1, -1);
        }

        graphicsCtx.drawImage(toDraw, 0, 0, transform.width, transform.height);

        if (flipped) {
            graphicsCtx.restore();
        }
    }

    /**
     * Returns the image to draw this frame: preview override wins, then the
     * animator's current frame, then the static image.
     */
    private Image resolveFrame() {
        if (previewOverride != null)
            return previewOverride;
        WritableImage frame = animator != null ? animator.getCurrentFrame() : null;
        return frame != null ? frame : staticImage;
    }

    public void previewFrame(WritableImage frame) {
        this.previewOverride = frame;
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
