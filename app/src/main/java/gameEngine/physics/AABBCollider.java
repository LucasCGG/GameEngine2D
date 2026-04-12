package gameEngine.physics;

import gameEngine.engine.Transform;

public class AABBCollider extends Collider {

    public float offsetX, offsetY;
    public float width, height;

    public AABBCollider(Transform transform) {
        super(transform);

        this.offsetX = 0;
        this.offsetY = 0;
        this.width = transform.width;
        this.height = transform.height;
    }

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
    public float getLeft() {
        return transform.x + offsetX;
    }

    public float getRight() {
        return transform.x + offsetX + width;
    }

    public float getTop() {
        return transform.y + offsetY;
    }

    public float getBottom() {
        return transform.y + offsetY + height;
    }

    @Override
    public Type getType() {
        return Type.AABB;
    }

    @Override
    public boolean intersects(Collider other) {
        if (other instanceof AABBCollider b) {
            return getLeft() < b.getLeft() && getRight() > b.getRight() && getTop() < b.getTop() && getBottom() > b.getBottom();
        }
        if (other instanceof CircleCollider c) {
            return c.intersects(this);
        }
        return false;
    }
}
