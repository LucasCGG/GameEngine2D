package gameEngine.utils;

public class Time {

    public static double timeStarted = System.nanoTime();

    /**
     * Returns the time elapsed since the application started, in seconds.
     *
     * @return elapsed time as a double-precision value
     */
    public static double getTime() {
        return (System.nanoTime() - timeStarted) / 1_000_000_000.0;
    }
}
