package gameEngine.entitys;

import gameEngine.attributes.Attributes;
// import gameEngine.audio.AudioManager;
import gameEngine.core.KeyListener;
import gameEngine.core.MouseListener;
import gameEngine.core.Transform;
import gameEngine.sprites.Animation;
import gameEngine.sprites.Animator;
import gameEngine.sprites.Sprite;
import gameEngine.sprites.SpriteSheet;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class Player extends Entity {

    private final Sprite sprite;
    private float speed;
    private float jumpSoundCooldown = 0f;
    // private static final float JUMP_SOUND_COOLDOWN = 0.5f;

    public Player(float x, float y) {
        super("Player", new Transform(x, y, 100, 100), new Attributes(100, 30, 10, 3));
        this.speed = attributes.movementSpeed;

        SpriteSheet sheet = new SpriteSheet("assets/images/32bit-PaperAirplane-Spritesheet.png", 1025, 1025);
        Animation idle = new Animation("idle", sheet, new int[] { 1 }, 0.4f, true);
        Animation walk = new Animation("walk", sheet, new int[] { 1, 2, 3, 4, 5, 6, 7, 8 }, 0.3f, true);
        // Animation jump = new Animation("jump", sheet, new int[] { 1 }, 0.15f, false);

        Animator animator = new Animator();
        animator.addAnimation(idle);
        animator.addAnimation(walk);
        animator.play("idle");

        sprite = new Sprite("player", animator, transform, true);
    }

    @Override
    public void update(float deltaTime) {
        boolean moving = false;
        KeyListener keys = KeyListener.get();

        // if (keys.isKeyDown(KeyCode.RIGHT) || keys.isKeyDown(KeyCode.D)) {
        // transform.x += speed * deltaTime;
        // moving = true;
        // }
        // if (keys.isKeyDown(KeyCode.LEFT) || keys.isKeyDown(KeyCode.A)) {
        // transform.x -= speed * deltaTime;
        // moving = true;
        // }
        // if (keys.isKeyDown(KeyCode.DOWN) || keys.isKeyDown(KeyCode.S)) {
        // transform.y += speed * deltaTime;
        // moving = true;
        // }
        // if (keys.isKeyDown(KeyCode.UP) || keys.isKeyDown(KeyCode.W)) {
        // transform.y -= speed * deltaTime;
        // moving = true;
        // }

        double inputX = 0, inputY = 0;
        if (keys.isKeyDown(KeyCode.RIGHT) || keys.isKeyDown(KeyCode.D)) {
            inputY += 1;
            moving = true;
        }
        if (keys.isKeyDown(KeyCode.LEFT) || keys.isKeyDown(KeyCode.A)) {
            inputY -= 1;
            moving = true;
        }
        if (keys.isKeyDown(KeyCode.DOWN) || keys.isKeyDown(KeyCode.S)) {
            inputX -= 1;
            moving = true;
        }
        if (keys.isKeyDown(KeyCode.UP) || keys.isKeyDown(KeyCode.W)) {
            inputX += 1;
            moving = true;
        }

        double cx = transform.x + transform.width / 2.0;
        double cy = transform.y + transform.height / 2.0;
        double mx = MouseListener.get().getX();
        double my = MouseListener.get().getY();
        double angleRad = Math.atan2(my - cy, mx - cx);
        transform.rotation = (float) Math.toDegrees(angleRad);

        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double worldX = inputX * cos - inputY * sin;
        double worldY = inputX * sin + inputY * cos;

        transform.x += worldX * speed * deltaTime;
        transform.y += worldY * speed * deltaTime;

        Animator anim = sprite.getAnimator();
        if (keys.isKeyDown(KeyCode.L)) {
            gainExperience(200);
        }

        jumpSoundCooldown = Math.max(0, jumpSoundCooldown - deltaTime);

        // if (keys.isKeyDown(KeyCode.SPACE)) {
        // anim.play("jump");
        // if (jumpSoundCooldown <= 0) {
        // AudioManager.get().playSound("jump");
        // jumpSoundCooldown = JUMP_SOUND_COOLDOWN;
        // }
        // } else if (anim.getCurrentName().equals("jump") && anim.isFinished()) {
        // anim.play("idle");
        if (moving) {
            anim.play("walk");
        } else {
            anim.play("idle");
        }

        sprite.update(deltaTime);
    }

    @Override
    protected void draw(GraphicsContext graphicsContext) {
        sprite.draw(graphicsContext);
    }

    public void gainExperience(int amount) {
        attributes.gainExperience(amount);
        speed = attributes.movementSpeed;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

}
