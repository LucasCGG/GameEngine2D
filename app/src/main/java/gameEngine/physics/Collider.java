package gameEngine.physics;

import gameEngine.core.Transform;

public abstract class Collider {

    public enum Type {
        AABB, CIRCLE
    }

    protected Transform transform;
    public boolean isTrigger = false;

    /**
     * Creates a collider bound to the given transform.
     *
     * @param transform the owning object's transform; used to track world
     * position
     */
    public Collider(Transform transform) {
        this.transform = transform;
    }

    /**
     * Returns the shape type of this collider.
     *
     * @return {@code Type.AABB} or {@code Type.CIRCLE}
     */
    public abstract Type getType();

    /**
     * Tests whether this collider overlaps with another.
     *
     * @param other the collider to test against
     * @return {@code true} if the two colliders intersect
     */
    public abstract boolean intersects(Collider other);
}
