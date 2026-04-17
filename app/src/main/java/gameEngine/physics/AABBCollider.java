package gameEngine.physics;

import gameEngine.core.Transform;

public class AABBCollider extends Collider {

    public float offsetX, offsetY;
    public float width, height;

    /**
     * Creates an axis-aligned bounding box that matches the transform exactly.
     *
     * @param transform the owning object's transform
     */
    public AABBCollider(Transform transform) {
        super(transform);

        this.offsetX = 0;
        this.offsetY = 0;
        this.width = transform.width;
        this.height = transform.height;
    }

    /**
     * Creates an axis-aligned bounding box with a custom size and offset.
     *
     * @param transform the owning object's transform
     * @param offsetX horizontal offset from the transform origin, in pixels
     * @param offsetY vertical offset from the transform origin, in pixels
     * @param width collider width in pixels
     * @param height collider height in pixels
     */
    public AABBCollider(Transform transform, float offsetX, float offsetY, float width, float height) {
        super(transform);

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    /**
     * World-space bounds
     */
    /**
     * @return world-space left edge
     */
    public float getLeft() {
        return transform.x + offsetX;
    }

    /**
     * @return world-space right edge
     */
    public float getRight() {
        return transform.x + offsetX + width;
    }

    /**
     * @return world-space top edge
     */
    public float getTop() {
        return transform.y + offsetY;
    }

    /**
     * @return world-space bottom edge
     */
    public float getBottom() {
        return transform.y + offsetY + height;
    }

    /**
     * Returns the shape type of this collider.
     *
     * @return {@code Type.AABB}
     */
    @Override
    public Type getType() {
        return Type.AABB;
    }

    /**
     * Tests whether this collider overlaps with another.
     * <p>
     * AABB vs AABB: uses the Separating Axis Theorem — two boxes overlap if and
     * only if they are not separated on either axis.
     * <p>
     * AABB vs Circle: delegates to {@link CircleCollider#intersects} to avoid
     * duplicating the closest-point logic.
     *
     * @param other the collider to test against
     * @return {@code true} if the two colliders intersect
     */
    @Override
    public boolean intersects(Collider other) {
        if (other instanceof AABBCollider b) {
            return getLeft() < b.getRight() && getRight() > b.getLeft() && getTop() < b.getBottom() && getBottom() > b.getTop();
        }
        if (other instanceof CircleCollider c) {
            return c.intersects(this);
        }
        return false;
    }
}
