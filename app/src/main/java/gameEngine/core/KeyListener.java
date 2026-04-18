package gameEngine.core;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyListener {

    private static KeyListener INSTANCE = new KeyListener();
    private boolean[] keyPressed = new boolean[KeyCode.values().length];
    private boolean[] keyJustReleased = new boolean[KeyCode.values().length];
    private boolean[] keyWasPressed = new boolean[KeyCode.values().length];

    private KeyListener() {
    }

    /**
     * Returns the singleton KeyListener instance, creating it if necessary.
     *
     * @return the global KeyListener instance
     */
    public static KeyListener get() {
        if (KeyListener.INSTANCE == null) {
            KeyListener.INSTANCE = new KeyListener();
        }
        return KeyListener.INSTANCE;
    }

    /**
     * Callbacks
     */
    /**
     * Returns an event handler that records key-press events. Wire this to
     * {@code scene.setOnKeyPressed()}.
     *
     * @return an EventHandler that marks the key as pressed
     */
    public EventHandler<KeyEvent> keyPressedHandler() {
        return e -> keyPressed[e.getCode().ordinal()] = true;
    }

    /**
     * Returns an event handler that records key-release events. Wire this to
     * {@code scene.setOnKeyReleased()}.
     *
     * @return an EventHandler that marks the key as released
     */
    public EventHandler<KeyEvent> keyReleaseHandler() {
        return e -> keyPressed[e.getCode().ordinal()] = false;
    }

    /**
     * Must be called once at the end of every frame to advance the just-pressed
     * / just-released state machine.
     */
    public void endFrame() {
        for (int i = 0; i < keyPressed.length; i++) {
            keyJustReleased[i] = keyWasPressed[i] && !keyPressed[i];
            keyWasPressed[i] = keyPressed[i];
        }
    }

    /**
     * Getters
     */
    /**
     * Returns whether the given key is currently held down.
     *
     * @param key the key to query
     * @return {@code true} if the key is pressed
     */
    public boolean isKeyDown(KeyCode key) {
        return keyPressed[key.ordinal()];
    }

    /**
     * Returns whether the given key is currently not pressed.
     *
     * @param key the key to query
     * @return {@code true} if the key is not pressed
     */
    public boolean isKeyUp(KeyCode key) {
        return !keyPressed[key.ordinal()];
    }

    /**
     * Returns whether the given key was pressed this frame (rising edge only).
     * Returns {@code true} for exactly one frame when the key is first pressed.
     *
     * @param key the key to query
     * @return {@code true} on the first frame the key is pressed
     */
    public boolean isKeyJustPressed(KeyCode key) {
        return keyPressed[key.ordinal()] && !keyWasPressed[key.ordinal()];
    }
}
