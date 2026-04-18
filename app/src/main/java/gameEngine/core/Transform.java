package gameEngine.core;

public class Transform {

    public float x, y;
    public float width, height;
    public float rotation;

    /**
     * Creates a transform with no rotation.
     *
     * @param x world-space horizontal position
     * @param y world-space vertical position
     * @param width object width in pixels
     * @param height object height in pixels
     */
    public Transform(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = 0;
    }

    /**
     * Creates a transform with an explicit rotation.
     *
     * @param x world-space horizontal position
     * @param y world-space vertical position
     * @param width object width in pixels
     * @param height object height in pixels
     * @param rotation rotation angle in degrees
     */
    public Transform(float x, float y, float width, float height, float rotation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    /**
     * Returns a new Transform with identical values to this one.
     *
     * @return a deep copy of this transform
     */
    public Transform copy() {
        return new Transform(x, y, width, height, rotation);
    }

    /**
     * Overwrites all fields of this transform with the values from another.
     *
     * @param other the source transform to copy from
     */
    public void copyFrom(Transform other) {
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
        this.rotation = other.rotation;
    }
}
