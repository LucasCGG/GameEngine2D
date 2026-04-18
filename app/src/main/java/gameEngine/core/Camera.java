package gameEngine.core;

public class Camera {

    public float x, y;
    public float smoothSpeed = 5.0f;

    public enum Mode {
        FIXED, FOLLOW
    }
    private Mode mode = Mode.FIXED;

    /**
     * Creates a camera positioned at the given world-space coordinates.
     *
     * @param x initial horizontal position
     * @param y initial vertical position
     */
    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Updates the camera position. In {@code FOLLOW} mode the camera smoothly
     * interpolates towards the centre of the target transform. In {@code FIXED}
     * mode this method does nothing.
     *
     * @param deltaTime elapsed time since the last frame, in seconds
     * @param target the transform to follow
     * @param screenWidth viewport width in pixels
     * @param screenHeight viewport height in pixels
     */
    public void update(float deltaTime, Transform target, int screenWidth, int screenHeight) {
        if (mode == Mode.FIXED) {
            return;
        }

        float targetX = target.x + target.width / 2.0f - screenWidth / 2.0f;
        float targetY = target.y + target.height / 2.0f - screenHeight / 2.0f;

        x += (targetX - x) * smoothSpeed * deltaTime;
        y += (targetY - y) * smoothSpeed * deltaTime;
    }

    /**
     * Toggles between {@code FIXED} and {@code FOLLOW} camera modes.
     */
    public void toggleMode() {
        mode = (mode == Mode.FIXED) ? Mode.FOLLOW : Mode.FIXED;
        System.out.println("Camera mode: " + mode);
    }

    /**
     * Returns the current camera mode.
     *
     * @return {@code Mode.FIXED} or {@code Mode.FOLLOW}
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Sets the interpolation speed used in {@code FOLLOW} mode. Higher values
     * make the camera snap more aggressively to the target.
     *
     * @param speed interpolation factor (typical range: 1–20)
     */
    public void setSmoothSpeed(float speed) {
        this.smoothSpeed = speed;
    }
}
