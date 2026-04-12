package gameEngine.engine;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseListener {

    private static MouseListener instance;
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

    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    /**
     * Callbacks (handlers)
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

    public EventHandler<MouseEvent> mousePressHandler() {
        return e -> {
            int btn = buttonIndex(e.getButton());
            if (btn >= 0) {
                mouseButtonPressed[btn] = true;
            }
        };
    }

    public EventHandler<MouseEvent> mouseReleaseHandler() {
        return e -> {
            int btn = buttonIndex(e.getButton());
            if (btn >= 0) {
                mouseButtonPressed[btn] = false;
            }
            isDragging = false;
        };
    }

    public EventHandler<ScrollEvent> mouseScrollHandler() {
        return e -> {
            scrollX = e.getDeltaX();
            scrollY = e.getDeltaY();
        };
    }

    public void endFrame() {
        scrollX = 0.0;
        scrollY = 0.0;
        lastX = xPos;
        lastY = yPos;
    }

    /**
     * Getters
     */
    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }

    public double getDeltaX() {
        return xPos - lastX;
    }

    public double getDeltaY() {
        return yPos - lastY;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public boolean isButtonDown(int btn) {
        return btn >= 0 && btn < mouseButtonPressed.length && mouseButtonPressed[btn];
    }

    /**
     * Helpers
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
