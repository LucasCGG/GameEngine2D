package gameEngine.physics;

import gameEngine.core.Transform;

public class CircleCollider extends Collider {

    public float radius;
    public float offsetX, offsetY;

    public CircleCollider(Transform transform) {
        super(transform);
        this.radius = Math.min(transform.width, transform.height) / 2.0f;
        this.offsetX = transform.width / 2.0f;
        this.offsetY = transform.height / 2.0f;
    }

    public CircleCollider(Transform transform, float radius, float offsetX, float offsetY) {
        super(transform);
        this.radius = radius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public float getCenterX() {
        return transform.x + offsetX;
    }

    public float getCenterY() {
        return transform.y + offsetY;
    }

    @Override
    public Type getType() {
        return Type.CIRCLE;
    }

    @Override
    public boolean intersects(Collider other) {
        if (other instanceof CircleCollider c) {
            float deltaX = getCenterX() - c.getCenterX();
            float deltaY = getCenterY() - c.getCenterY();
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            return distance < (radius + c.radius);
        }

        if (other instanceof AABBCollider b) {
            float closestX = Math.max(b.getLeft(), Math.min(getCenterX(), b.getRight()));
            float closestY = Math.max(b.getTop(), Math.min(getCenterY(), b.getBottom()));
            float deltaX = getCenterX() - closestX;
            float deltaY = getCenterY() - closestY;
            return (deltaX * deltaX + deltaY * deltaY) < (radius * radius);
        }
        return false;
    }
}
