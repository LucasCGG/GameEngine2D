package gameEngine.core;

public class Camera {

    public float x, y;
    public float smoothSpeed = 5.0f;

    public enum Mode {
        FIXED, FOLLOW
    }
    private Mode mode = Mode.FIXED;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float deltaTime, Transform target, int screeenWidth, int screenHeight) {
        if (mode == Mode.FIXED) {
            return;
        }

        float targetX = target.x + target.width / 2.0f - screeenWidth / 2.0f;
        float targetY = target.y + target.height / 2.0f - screenHeight / 2.0f;

        x += (targetX - x) * smoothSpeed * deltaTime;
        y += (targetY - y) * smoothSpeed * deltaTime;
    }

    public void toggleMode() {
        mode = (mode == Mode.FIXED) ? Mode.FOLLOW : Mode.FIXED;
        System.out.println("Camera mode: " + mode);
    }

    public Mode getMode() {
        return mode;
    }

    public void setSmoothSpeed(float speed) {
        this.smoothSpeed = speed;
    }
}
