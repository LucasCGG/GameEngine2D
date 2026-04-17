package gameEngine.core;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {

    private static int idCounter = 0;

    public final int id;
    public String name;
    public Transform transform;
    private boolean alive = true;

    public GameObject(String name, Transform transform) {
        this.id = idCounter++;
        this.name = name;
        this.transform = transform;
    }

    /**
     * Updates the object's logic for the current frame.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     */
    public abstract void update(float deltaTime);

    /**
     * Applies the camera transform and delegates to {@link #draw}. Does nothing
     * if the object has been destroyed.
     *
     * @param graphicsCtx the GraphicsContext to draw onto
     * @param camera the active camera, used to compute screen-space position
     */
    public void render(GraphicsContext graphicsCtx, Camera camera) {
        if (!alive) {
            return;
        }
        graphicsCtx.save();
        double screenX = transform.x - camera.x;
        double screenY = transform.y - camera.y;

        double cx = screenX + transform.width / 2.0;
        double cy = screenY + transform.height / 2.0;
        graphicsCtx.translate(cx, cy);
        graphicsCtx.rotate(transform.rotation);
        graphicsCtx.translate(-transform.width / 2, -transform.height / 2.0);

        draw(graphicsCtx);
        graphicsCtx.restore();
    }

    /**
     * Draws the object at its current local position. Called by {@link #render}
     * after the camera transform has been applied.
     *
     * @param graphicsCtx the GraphicsContext to draw onto
     */
    protected abstract void draw(GraphicsContext graphicsCtx);

    /**
     * Marks this object as destroyed. It will no longer be rendered and will be
     * removed from the collision world on the next frame.
     */
    public void destroy() {
        this.alive = false;
    }

    /**
     * Returns whether this object is still alive (i.e. not destroyed).
     *
     * @return {@code true} if the object is active, {@code false} if destroyed
     */
    public boolean isAlive() {
        return this.alive;
    }
}
