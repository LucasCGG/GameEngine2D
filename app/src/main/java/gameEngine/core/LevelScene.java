package gameEngine.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LevelScene extends GameScene {

    public LevelScene() {
        System.out.println("INSIDE LevelScene");
        Window.setClearColor(Color.WHITE);
    }

    @Override
    public void onResize(int width, int height) {

    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render(GraphicsContext graphicsCtx) {
    }
}
