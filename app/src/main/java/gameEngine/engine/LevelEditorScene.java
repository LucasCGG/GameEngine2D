package gameEngine.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gameEngine.audio.AudioManager;
import gameEngine.physics.AABBCollider;
import gameEngine.physics.CircleCollider;
import gameEngine.physics.Collider;
import gameEngine.physics.CollisionResult;
import gameEngine.sprites.Animation;
import gameEngine.sprites.Animator;
import gameEngine.sprites.Sprite;
import gameEngine.sprites.SpriteSheet;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LevelEditorScene extends GameScene {

    private List<GameObject> gameObjects = new ArrayList<>();
    private Set<String> collidingObjects = new HashSet<>();
    private Sprite player;
    private Sprite item;
    private float fps;
    private float speed = 300;

    private Map<String, Float> soundCooldowns = new HashMap<>();
    private static final float HIT_SOUND_COOLDOWN = 0.5f; // sec

    public LevelEditorScene() {
    }

    @Override
    public void init(int width, int height) {
        System.out.println("LevelEditorScene initialized: " + width + "x" + height);
        super.init(width, height);

        // Load audio
        AudioManager audio = AudioManager.get();
        audio.loadMusic("bgm", "assets/audio/back_to_nature.mp3");
        audio.loadSound("jump", "assets/audio/jumpland44100.mp3");
        audio.loadSound("hit", "assets/audio/metalthunk.mp3");

        audio.playMusic("bgm");

        buildVolumePanel();

        /**
         * Static sprite
         */
        //player = new Sprite("player", "assets/images/platformChar_idle.png", new Transform(100, 100, 128, 128));
        //gameObjects.add(player);
        item = new Sprite("rock", "assets/images/platformPack_item005.png",
                new Transform(400, 400, 64, 64));
        gameObjects.add(item);
        /**
         * Animator SpriteSheet
         */
        SpriteSheet sheet = new SpriteSheet("assets/images/platformerPack_character.png", 100, 100);

        Animation idle = new Animation("idle", sheet, new int[]{0, 6}, 0.4f, true);
        Animation walk = new Animation("walk", sheet, new int[]{2, 3}, 0.15f, true);
        Animation jump = new Animation("jump", sheet, new int[]{1}, 0.15f, false);

        Animator animator = new Animator();
        animator.addAnimation(idle);
        animator.addAnimation(walk);
        animator.addAnimation(jump);
        animator.play("idle");

        player = new Sprite("player", animator, new Transform(100, 100, 128, 128));
        gameObjects.add(player);

        collisionWorld.register(player, new AABBCollider(player.transform));
        collisionWorld.register(item, new CircleCollider(item.transform));
    }

    @Override
    public void update(float deltaTime) {
        fps = 1.0f / deltaTime;

        boolean moving = false;
        updateCooldowns(deltaTime);

        if (KeyListener.get().isKeyDown(KeyCode.RIGHT) || KeyListener.get().isKeyDown(KeyCode.D)) {
            player.transform.x += speed * deltaTime;
            moving = true;
        }
        if (KeyListener.get().isKeyDown(KeyCode.LEFT) || KeyListener.get().isKeyDown(KeyCode.A)) {
            player.transform.x -= speed * deltaTime;
            moving = true;
        }
        if (KeyListener.get().isKeyDown(KeyCode.DOWN) || KeyListener.get().isKeyDown(KeyCode.S)) {
            player.transform.y += speed * deltaTime;
            moving = true;
        }
        if (KeyListener.get().isKeyDown(KeyCode.UP) || KeyListener.get().isKeyDown(KeyCode.W)) {
            player.transform.y -= speed * deltaTime;
            moving = true;
        }

        Animator anim = player.getAnimator();

        if (KeyListener.get().isKeyDown(KeyCode.SPACE)) {
            anim.play("jump");
            playSoundWithCooldown("jump", deltaTime);
        } else if (anim.getCurrentName().equals("jump") && anim.isFinished()) {
            anim.play("idle");
        } else if (moving) {
            anim.play("walk");
        } else {
            anim.play("idle");
        }

        // Testing tranform rotation can/ should be removed later on
        //double cx = player.transform.x + player.transform.width / 2.0;
        //double cy = player.transform.y + player.transform.height / 2.0;
        //double mx = MouseListener.get().getX();
        //double my = MouseListener.get().getY();
        //double angleRad = Math.atan2(my - cy, mx - cx);
        //double angleDeg = Math.toDegrees(angleRad);
        //player.transform.rotation = (float) angleDeg;
        camera.update(deltaTime, player.transform, width, height);
        if (KeyListener.get().isKeyDown(KeyCode.C)) {
            camera.toggleMode();
        }

        // Scene switch logic
        //if (!changingScene && KeyListener.get().isKeyDown(KeyCode.SPACE)) {
        //    changingScene = true;
        //}
        //if (changingScene && timeToChangeScene > 0) {
        //   timeToChangeScene -= deltaTime;
        //} else if (changingScene) {
        //    Window.changeScene(1);
        //}
        collidingObjects.clear();
        for (CollisionResult result : collisionWorld.checkCollisions()) {
            if (result.isTrigger) {
                System.out.println(result.a.name + " triggered " + result.b.name);
            } else {
                collidingObjects.add(result.a.name);
                collidingObjects.add(result.b.name);
                playSoundWithCooldown("hit", deltaTime);
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

    private void renderColliders(GraphicsContext graphicsCtx) {
        graphicsCtx.setLineWidth(1);

        for (GameObject obj : gameObjects) {
            Collider c = collisionWorld.getCollider(obj);
            boolean hitting = collidingObjects.contains(obj.name);

            if (c instanceof AABBCollider a) {
                graphicsCtx.setStroke(hitting ? Color.RED : Color.GREEN);
                graphicsCtx.strokeRect(a.getLeft() - camera.x, a.getTop() - camera.y, a.width, a.height);
            } else if (c instanceof CircleCollider cr) {
                graphicsCtx.setStroke(hitting ? Color.RED : Color.GREEN);
                graphicsCtx.strokeOval(cr.getCenterX() - cr.radius - camera.x, cr.getCenterY() - cr.radius - camera.y, cr.radius * 2, cr.radius * 2);
            }

        }
    }

    private void playSoundWithCooldown(String name, float deltaTime) {
        soundCooldowns.merge(name, 0f, (oldVal, zero) -> oldVal); // ensure key exists
        float remaining = soundCooldowns.getOrDefault(name, 0f);
        if (remaining <= 0) {
            AudioManager.get().playSound(name);
            soundCooldowns.put(name, HIT_SOUND_COOLDOWN);
        }
    }

    private void updateCooldowns(float deltaTime) {
        soundCooldowns.replaceAll((name, remaining) -> Math.max(0, remaining - deltaTime));
    }

    public void buildVolumePanel() {
        Slider masterSlider = createSlider("Master", 1.0);
        masterSlider.valueProperty().addListener((obs, olVal, newVal)
                -> AudioManager.get().setMasterVolume(newVal.floatValue()));

        Slider sfxSlider = createSlider("SFX", 1.0);
        sfxSlider.valueProperty().addListener((obs, olVal, newVal)
                -> AudioManager.get().setSfxVolume(newVal.floatValue()));

        Slider musicSlider = createSlider("Music", 1.0);
        musicSlider.valueProperty().addListener((obs, olVal, newVal)
                -> AudioManager.get().setMusicVolume(newVal.floatValue()));

        VBox panel = new VBox(8,
                label("Master"), masterSlider,
                label("Music"), musicSlider,
                label("SFX"), sfxSlider
        );
        panel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.6);"
                + "-fx-padding: 12;"
                + "-fx-background-radius: 8;"
        );
        panel.setMaxSize(200, 160);

        StackPane.setAlignment(panel, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(panel, new javafx.geometry.Insets(10));

        Window.getRoot().getChildren().add(panel);
    }

    private Slider createSlider(String name, double initialValue) {
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
