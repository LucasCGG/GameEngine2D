package gameEngine.engine;

import gameEngine.utils.Time;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Window extends Application {

    private int width, height;
    private final String title;
    private Color clearColor = Color.BLACK;
    private static Window window = null;
    private static GameScene currentScene = null;

    private static StackPane root;

    public Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Game";
        clearColor = Color.WHITESMOKE;
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0 -> {
                currentScene = new LevelEditorScene();
                currentScene.init(window.width, window.height);
            }
            case 1 -> {
                currentScene = new LevelScene();
                currentScene.init(window.width, window.height);
            }
            default ->
                throw new IllegalArgumentException("Unknown scene '" + newScene + "'");
        }
    }

    public static void setClearColor(Color color) {
        get().clearColor = color;
    }

    public static Color getClearColor() {
        return get().clearColor;
    }

    @Override
    public void start(Stage stage) {
        System.out.println("Hello FXML!");

        window = this;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext graphicsCtx = canvas.getGraphicsContext2D();

        root = new StackPane(canvas);
        Scene scene = new Scene(root, width, height);

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            window.width = newVal.intValue();
            if (currentScene != null) {
                currentScene.onResize(window.width, window.height);
            }
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            window.height = newVal.intValue();
            if (currentScene != null) {
                currentScene.onResize(window.width, window.height);
            }
        });

        // Wire up listeners
        MouseListener mouse = MouseListener.get();
        scene.setOnMouseMoved(e -> {
            mouse.mouseMoveHandler().handle(e);
            //System.out.println("Mouse moved: (" + e.getX() + ", " + e.getY() + ")");
        });
        scene.setOnMouseDragged(e -> {
            mouse.mouseMoveHandler().handle(e);
            //System.out.println("Mouse dragged: (" + e.getX() + ", " + e.getY() + ")");
        });
        scene.setOnMousePressed(e -> {
            mouse.mousePressHandler().handle(e);
            //System.out.println("Mouse pressed: " + e.getButton());
        });
        scene.setOnMouseReleased(e -> {
            mouse.mouseReleaseHandler().handle(e);
            //System.out.println("Mouse released: " + e.getButton());
        });
        scene.setOnScroll(e -> {
            mouse.mouseScrollHandler().handle(e);
            //System.out.println("Mouse scroll: (" + e.getDeltaX() + ", " + e.getDeltaY() + ")");
        });

        KeyListener keys = KeyListener.get();
        scene.setOnKeyPressed(e -> {
            keys.keyPressedHandler().handle(e);
            //System.out.println("Key pressed: " + e.getCode());
        });
        scene.setOnKeyReleased(e -> {
            keys.keyRealeseHandler().handle(e);
            //System.out.println("Key released: " + e.getCode());
        });

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();

        changeScene(0);

        loop(graphicsCtx);
    }

    private void loop(GraphicsContext graphicsCtx) {
        AnimationTimer loop = new AnimationTimer() {
            double lastTime = Time.getTime();

            @Override
            public void handle(long now) {
                double deltaTime = Time.getTime() - lastTime;
                lastTime = Time.getTime();

                graphicsCtx.setFill(clearColor);
                graphicsCtx.fillRect(0, 0, window.width, window.height);

                if (currentScene != null) {
                    currentScene.update((float) deltaTime);
                    currentScene.render(graphicsCtx);
                }

                KeyListener.get().endFrame();
                MouseListener.get().endFrame();
            }
        };
        loop.start();
    }

    public static StackPane getRoot() {
        return root;
    }
}
