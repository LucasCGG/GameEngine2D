package gameEngine.engine;

import gameEngine.attributes.Attributes;
import gameEngine.audio.AudioManager;
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
    private static final float JUMP_SOUND_COOLDOWN = 0.5f;

    public Player(float x, float y) {
        super("Player", new Transform(x, y, 100, 100), new Attributes(100, 5, 10, 3));
        this.speed = attributes.movementSpeed;

        SpriteSheet sheet = new SpriteSheet("assets/images/platformerPack_character.png", 100, 100);
        Animation idle = new Animation("idle", sheet, new int[]{0, 0}, 0.4f, true);
        Animation walk = new Animation("walk", sheet, new int[]{2, 2}, 0.15f, true);
        Animation jump = new Animation("jump", sheet, new int[]{1}, 0.15f, false);

        Animator animator = new Animator();
        animator.addAnimation(idle);
        animator.addAnimation(walk);
        animator.addAnimation(jump);
        animator.play("idle");

        sprite = new Sprite("player", animator, transform);
    }

    @Override
    public void update(float deltaTime) {
        boolean moving = false;
        KeyListener keys = KeyListener.get();

        if (keys.isKeyDown(KeyCode.RIGHT) || keys.isKeyDown(KeyCode.D)) {
            transform.x += speed * deltaTime;
            moving = true;
        }
        if (keys.isKeyDown(KeyCode.LEFT) || keys.isKeyDown(KeyCode.A)) {
            transform.x -= speed * deltaTime;
            moving = true;
        }
        if (keys.isKeyDown(KeyCode.DOWN) || keys.isKeyDown(KeyCode.S)) {
            transform.y += speed * deltaTime;
            moving = true;
        }
        if (keys.isKeyDown(KeyCode.UP) || keys.isKeyDown(KeyCode.W)) {
            transform.y -= speed * deltaTime;
            moving = true;
        }

        Animator anim = sprite.getAnimator();
        if (keys.isKeyDown(KeyCode.L)) {
            gainExperience(200);
        }

        jumpSoundCooldown = Math.max(0, jumpSoundCooldown - deltaTime);

        if (keys.isKeyDown(KeyCode.SPACE)) {
            anim.play("jump");
            if (jumpSoundCooldown <= 0) {
                AudioManager.get().playSound("jump");
                jumpSoundCooldown = JUMP_SOUND_COOLDOWN;
            }
        } else if (anim.getCurrentName().equals("jump") && anim.isFinished()) {
            anim.play("idle");
        } else if (moving) {
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

    public Sprite getSprite() {
        return sprite;
    }
}
