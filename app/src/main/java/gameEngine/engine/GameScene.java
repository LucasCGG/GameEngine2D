package gameEngine.engine;

import gameEngine.physics.CollisionWorld;
import javafx.scene.canvas.GraphicsContext;

public abstract class GameScene {

    protected boolean isRunning = false;
    protected int width, height;
    protected Camera camera;
    protected CollisionWorld collisionWorld;

    public GameScene() {

    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.camera = new Camera(0, 0);
        this.collisionWorld = new CollisionWorld();
    }

    public abstract void update(float deltaTime);

    public abstract void render(GraphicsContext graphicsCtx);
}
