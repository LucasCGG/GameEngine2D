package gameEngine.sprites;

import gameEngine.core.GameObject;
import gameEngine.core.Transform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Sprite extends GameObject {

    private Image staticImage; // use if static image
    private Animator animator; // use if animated

    public Sprite(String name, String imagePath, Transform transform) {
        super(name, transform);
        this.staticImage = AssetPool.getImage(imagePath);
    }

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

    public Animator getAnimator() {
        return animator;
    }
}