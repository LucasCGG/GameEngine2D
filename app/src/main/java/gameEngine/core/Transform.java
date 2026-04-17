package gameEngine.core;

public class Transform {

    public float x, y;
    public float width, height;
    public float rotation;

    public Transform(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = 0;
    }

    public Transform(float x, float y, float width, float height, float rotation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    public Transform copy() {
        return new Transform(x, y, width, height, rotation);
    }

    public void copyFrom(Transform other) {
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
        this.rotation = other.rotation;
    }
}
