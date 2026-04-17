package gameEngine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gameEngine.attributes.Attributes;
import gameEngine.audio.AudioManager;
import gameEngine.entitys.Player;
import gameEngine.physics.AABBCollider;
import gameEngine.physics.CircleCollider;
import gameEngine.physics.Collider;
import gameEngine.physics.CollisionResult;
import gameEngine.sprites.Sprite;
import gameEngine.sprites.SpriteSheet;
import gameEngine.sprites.SpriteSheetPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LevelEditorScene extends GameScene {

    private List<GameObject> gameObjects = new ArrayList<>();
    private Set<String> collidingObjects = new HashSet<>();
    private Player player;
    private Sprite item;
    private float fps;

    private Map<String, Float> soundCooldowns = new HashMap<>();
    private static final float HIT_SOUND_COOLDOWN = 0.5f;

    public LevelEditorScene() {
    }

    @Override
    public void init(int width, int height) {
        System.out.println("LevelEditorScene initialized: " + width + "x" + height);
        super.init(width, height);

        AudioManager audio = AudioManager.get();
        audio.loadMusic("bgm", "assets/audio/back_to_nature.mp3");
        audio.loadSound("jump", "assets/audio/jumpland44100.mp3");
        audio.loadSound("hit", "assets/audio/metalthunk.mp3");
        audio.playMusic("bgm");

        buildVolumePanel();
        showAttributes();

        item = new Sprite("rock", "assets/images/platformPack_item005.png",
                new Transform(400, 400, 64, 64));
        gameObjects.add(item);

        player = new Player(100, 100);
        gameObjects.add(player.getSprite());

        
        collisionWorld.register(player.getSprite(), new AABBCollider(player.transform));
        collisionWorld.register(item, new CircleCollider(item.transform));

        SpriteSheetPanel.register("Player",
                new SpriteSheet("assets/images/32bit-PaperAirplane-Spritesheet.png", 1025,
                        1025));
        SpriteSheetPanel.register("Rock",
                new SpriteSheet("assets/images/platformPack_item005.png", 64, 64));
        SpriteSheetPanel.build(Window.getRoot());
    }

    @Override
    public void update(float deltaTime) {
        fps = 1.0f / deltaTime;
        updateCooldowns(deltaTime);

        player.update(deltaTime);

        camera.update(deltaTime, player.transform, width, height);
        if (KeyListener.get().isKeyJustPressed(KeyCode.C)) {
            camera.toggleMode();
        }

        SpriteSheetPanel.handleInput(KeyListener.get());

        collidingObjects.clear();
        for (CollisionResult result : collisionWorld.checkCollisions()) {
            if (result.isTrigger) {
                System.out.println(result.a.name + " triggered " + result.b.name);
            } else {
                collidingObjects.add(result.a.name);
                collidingObjects.add(result.b.name);
                playSoundWithCooldown("hit");
                System.out.println(result.a.name + " collided with " + result.b.name);
            }
        }
    }

    @Override
    public void render(GraphicsContext graphicsCtx) {
        for (GameObject obj : gameObjects) {
            obj.render(graphicsCtx, camera);
        }

        graphicsCtx.setFill(Color.GREEN);
        graphicsCtx.setFont(javafx.scene.text.Font.font("Arial", 20));
        graphicsCtx.fillText(String.format("FPS: %.0f", fps), 10, height - 30);
        graphicsCtx.fillText("Camera: " + camera.getMode(), 10, height - 10);

        renderColliders(graphicsCtx);
    }

    @Override
    public void onResize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void renderColliders(GraphicsContext graphicsCtx) {
        graphicsCtx.setLineWidth(1);

        for (GameObject obj : gameObjects) {
            Collider c = collisionWorld.getCollider(obj);
            boolean hitting = collidingObjects.contains(obj.name);

            switch (c) {
                case AABBCollider a -> {
                    graphicsCtx.setStroke(hitting ? Color.RED : Color.GREEN);
                    graphicsCtx.strokeRect(a.getLeft() - camera.x, a.getTop() - camera.y, a.width, a.height);
                }
                case CircleCollider cr -> {
                    graphicsCtx.setStroke(hitting ? Color.RED : Color.GREEN);
                    graphicsCtx.strokeOval(cr.getCenterX() - cr.radius - camera.x,
                            cr.getCenterY() - cr.radius - camera.y, cr.radius * 2, cr.radius * 2);
                }
                default -> {
                }
            }
        }
    }

    private void playSoundWithCooldown(String name) {
        soundCooldowns.merge(name, 0f, (oldVal, zero) -> oldVal);
        float remaining = soundCooldowns.getOrDefault(name, 0f);
        if (remaining <= 0) {
            AudioManager.get().playSound(name);
            soundCooldowns.put(name, HIT_SOUND_COOLDOWN);
        }
    }

    private void updateCooldowns(float deltaTime) {
        soundCooldowns.replaceAll((name, remaining) -> Math.max(0, remaining - deltaTime));
    }

    public void showAttributes() {
        Label healthLabel = new Label();
        Label levelLabel = new Label();
        Label expLabel = new Label();
        Label damageLabel = new Label();
        Label defenseLabel = new Label();
        Label speedLabel = new Label();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            Attributes a = player.attributes;
            healthLabel.setText("❤  HP:  " + (int) a.currentHealth + " / " + (int) a.maxHealth);
            levelLabel.setText("⭐ LVL: " + a.level);
            expLabel.setText("✨ EXP: " + a.experience + " / " + a.experienceToNextLevel);
            damageLabel.setText("⚔  DMG: " + (int) a.damage);
            defenseLabel.setText("🛡 DEF: " + (int) a.defense);
            speedLabel.setText("👟 SPD: " + String.format("%.1f", a.movementSpeed));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        String labelStyle = "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: monospace;";
        for (Label l : new Label[] { healthLabel, levelLabel, expLabel, damageLabel, defenseLabel, speedLabel }) {
            l.setStyle(labelStyle);
        }

        VBox panel = new VBox(5, healthLabel, levelLabel, expLabel, damageLabel, defenseLabel, speedLabel);
        panel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.6);"
                        + "-fx-padding: 12;"
                        + "-fx-background-radius: 8;");
        panel.setMaxSize(200, 180);

        StackPane.setAlignment(panel, javafx.geometry.Pos.BOTTOM_RIGHT);
        StackPane.setMargin(panel, new javafx.geometry.Insets(10));

        Window.getRoot().getChildren().add(panel);
    }

    public void buildVolumePanel() {
        Slider masterSlider = createSlider(1.0);
        masterSlider.valueProperty()
                .addListener((obs, oldVal, newVal) -> AudioManager.get().setMasterVolume(newVal.floatValue()));

        Slider sfxSlider = createSlider(1.0);
        sfxSlider.valueProperty()
                .addListener((obs, oldVal, newVal) -> AudioManager.get().setSfxVolume(newVal.floatValue()));

        Slider musicSlider = createSlider(1.0);
        musicSlider.valueProperty()
                .addListener((obs, oldVal, newVal) -> AudioManager.get().setMusicVolume(newVal.floatValue()));

        VBox panel = new VBox(8,
                label("Master"), masterSlider,
                label("Music"), musicSlider,
                label("SFX"), sfxSlider);
        panel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.6);"
                        + "-fx-padding: 12;"
                        + "-fx-background-radius: 8;");
        panel.setMaxSize(200, 160);

        StackPane.setAlignment(panel, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(panel, new javafx.geometry.Insets(10));

        Window.getRoot().getChildren().add(panel);
    }

    private Slider createSlider(double initialValue) {
        Slider slider = new Slider(0.0, 1.0, initialValue);
        slider.setShowTickMarks(false);
        slider.setPrefWidth(180);
        return slider;
    }

    private javafx.scene.control.Label label(String text) {
        javafx.scene.control.Label l = new javafx.scene.control.Label(text);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        return l;
    }
}
