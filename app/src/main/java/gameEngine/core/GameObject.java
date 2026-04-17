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

    public abstract void update(float deltaTime);

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

    protected abstract void draw(GraphicsContext graphicsCtx);

    public void destroy() {
        this.alive = false;
    }

    public boolean isAlive() {
        return this.alive;
    }
}
