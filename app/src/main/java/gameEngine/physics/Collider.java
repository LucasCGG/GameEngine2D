package gameEngine.physics;

import gameEngine.core.Transform;

public abstract class Collider {

    public enum Type {
        AABB, CIRCLE
    }

    protected Transform transform;
    public boolean isTrigger = false;

    public Collider(Transform transform) {
        this.transform = transform;
    }

    public abstract Type getType();

    public abstract boolean intersects(Collider other);
}
