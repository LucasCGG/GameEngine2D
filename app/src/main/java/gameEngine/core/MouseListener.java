package gameEngine.core;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseListener {

    private static MouseListener INSTANCE = new MouseListener();
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    /**
     * Returns the singleton MouseListener instance, creating it if necessary.
     *
     * @return the global MouseListener instance
     */
    public static MouseListener get() {
        if (MouseListener.INSTANCE == null) {
            MouseListener.INSTANCE = new MouseListener();
        }

        return MouseListener.INSTANCE;
    }

    /**
     * Callbacks (handlers)
     */
    /**
     * Returns an event handler for mouse move and drag events.
     *
     * @return an EventHandler that updates cursor position and dragging state
     */
    public EventHandler<MouseEvent> mouseMoveHandler() {
        return e -> {
            lastX = xPos;
            lastY = yPos;
            xPos = e.getX();
            yPos = e.getY();
            isDragging = e.isPrimaryButtonDown() || e.isSecondaryButtonDown() || e.isMiddleButtonDown();
        };
    }

    /**
     * Returns an event handler for mouse button press events.
     *
     * @return an EventHandler that marks the pressed button as down
     */
    public EventHandler<MouseEvent> mousePressHandler() {
        return e -> {
            int btn = buttonIndex(e.getButton());
            if (btn >= 0) {
                mouseButtonPressed[btn] = true;
            }
        };
    }

    /**
     * Returns an event handler for mouse button release events.
     *
     * @return an EventHandler that marks the released button as up
     */
    public EventHandler<MouseEvent> mouseReleaseHandler() {
        return e -> {
            int btn = buttonIndex(e.getButton());
            if (btn >= 0) {
                mouseButtonPressed[btn] = false;
            }
            isDragging = false;
        };
    }

    /**
     * Returns an event handler for scroll wheel events.
     *
     * @return an EventHandler that records scroll delta values
     */
    public EventHandler<ScrollEvent> mouseScrollHandler() {
        return e -> {
            scrollX = e.getDeltaX();
            scrollY = e.getDeltaY();
        };
    }

    /**
     * Must be called once at the end of every frame to reset scroll deltas and
     * update the previous cursor position.
     */
    public void endFrame() {
        scrollX = 0.0;
        scrollY = 0.0;
        lastX = xPos;
        lastY = yPos;
    }

    /**
     * Getters
     */
    /**
     * @return current cursor X position in scene coordinates
     */
    public double getX() {
        return xPos;
    }

    /**
     * @return current cursor Y position in scene coordinates
     */
    public double getY() {
        return yPos;
    }

    /**
     * @return horizontal cursor movement since the last frame
     */
    public double getDeltaX() {
        return xPos - lastX;
    }

    /**
     * @return vertical cursor movement since the last frame
     */
    public double getDeltaY() {
        return yPos - lastY;
    }

    /**
     * @return horizontal scroll delta this frame
     */
    public double getScrollX() {
        return scrollX;
    }

    /**
     * @return vertical scroll delta this frame
     */
    public double getScrollY() {
        return scrollY;
    }

    /**
     * Returns whether the cursor is currently being dragged (moved while any
     * button is held).
     *
     * @return {@code true} if dragging
     */
    public boolean isDragging() {
        return isDragging;
    }

    /**
     * Returns whether the given mouse button index is currently held down.
     *
     * @param btn button index: 0 = primary, 1 = secondary, 2 = middle
     * @return {@code true} if the button is pressed
     */
    public boolean isButtonDown(int btn) {
        return btn >= 0 && btn < mouseButtonPressed.length && mouseButtonPressed[btn];
    }

    /**
     * Helpers
     */
    /**
     * Converts a JavaFX {@link MouseButton} to the internal button index.
     *
     * @param btn the JavaFX button constant
     * @return 0 for PRIMARY, 1 for SECONDARY, 2 for MIDDLE, -1 for others
     */
    public int buttonIndex(MouseButton btn) {
        return switch (btn) {
            case PRIMARY ->
                0;
            case SECONDARY ->
                1;
            case MIDDLE ->
                2;
            default ->
                -1;
        };
    }
}
