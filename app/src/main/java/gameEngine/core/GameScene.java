package gameEngine.core;

import gameEngine.physics.CollisionWorld;
import javafx.scene.canvas.GraphicsContext;

public abstract class GameScene {

    protected boolean isRunning = false;
    protected int width, height;
    protected Camera camera;
    protected CollisionWorld collisionWorld;

    public GameScene() {

    }

    /**
     * Initialises the scene with the given viewport dimensions. Creates the
     * camera and collision world. Must be called before the first update/render
     * cycle.
     *
     * @param width viewport width in pixels
     * @param height viewport height in pixels
     */
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.camera = new Camera(0, 0);
        this.collisionWorld = new CollisionWorld();
    }

    /**
     * Updates all game logic for the current frame.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     */
    public abstract void update(float deltaTime);

    /**
     * Renders all visible objects to the canvas.
     *
     * @param graphicsCtx the GraphicsContext to draw onto
     */
    public abstract void render(GraphicsContext graphicsCtx);

    /**
     * Called whenever the window is resized. Implementations should update any
     * layout or projection that depends on viewport size.
     *
     * @param width new viewport width in pixels
     * @param height new viewport height in pixels
     */
    public abstract void onResize(int width, int height);
}
