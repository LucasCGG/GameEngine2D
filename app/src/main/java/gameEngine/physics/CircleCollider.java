package gameEngine.physics;

import gameEngine.core.Transform;

public class CircleCollider extends Collider {

    public float radius;
    public float offsetX, offsetY;

    /**
     * Creates a circle collider sized to the smaller of the transform's width
     * and height, centred on the transform.
     *
     * @param transform the owning object's transform
     */
    public CircleCollider(Transform transform) {
        super(transform);
        this.radius = Math.min(transform.width, transform.height) / 2.0f;
        this.offsetX = transform.width / 2.0f;
        this.offsetY = transform.height / 2.0f;
    }

    /**
     * Creates a circle collider with explicit radius and centre offset.
     *
     * @param transform the owning object's transform
     * @param radius circle radius in pixels
     * @param offsetX horizontal offset from the transform origin to the circle
     * centre
     * @param offsetY vertical offset from the transform origin to the circle
     * centre
     */
    public CircleCollider(Transform transform, float radius, float offsetX, float offsetY) {
        super(transform);
        this.radius = radius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Returns the world-space X coordinate of the circle centre.
     *
     * @return centre X in pixels
     */
    public float getCenterX() {
        return transform.x + offsetX;
    }

    /**
     * Returns the world-space Y coordinate of the circle centre.
     *
     * @return centre Y in pixels
     */
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
