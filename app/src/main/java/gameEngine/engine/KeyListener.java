package gameEngine.engine;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyListener {

    private static KeyListener instance;
    private boolean[] keyPressed = new boolean[KeyCode.values().length];
    private boolean[] keyJustReleased = new boolean[KeyCode.values().length];
    private boolean[] keyWasPressed = new boolean[KeyCode.values().length];

    private KeyListener() {
    }

    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    /**
     * Callbacks
     */
    public EventHandler<KeyEvent> keyPressedHandler() {
        return e -> keyPressed[e.getCode().ordinal()] = true;
    }

    public EventHandler<KeyEvent> keyRealeseHandler() {
        return e -> keyPressed[e.getCode().ordinal()] = false;
    }

    public void endFrame() {
        for (int i = 0; i < keyPressed.length; i++) {
            keyJustReleased[i] = keyWasPressed[i] && !keyPressed[i];
            keyWasPressed[i] = keyPressed[i];
        }
    }

    /**
     * Getters
     */
    public boolean isKeyDown(KeyCode key) {
        return keyPressed[key.ordinal()];
    }

    public boolean isKeyUp(KeyCode key) {
        return keyPressed[key.ordinal()];
    }

    public boolean isKeyJustPressed(KeyCode key) {
        return keyPressed[key.ordinal()] && !keyWasPressed[key.ordinal()];
    }

}
