package gameEngine.physics;

import java.util.ArrayList;
import java.util.List;

import gameEngine.core.GameObject;

public class CollisionWorld {

    private List<ColliderEntry> entries = new ArrayList<>();

    private static class ColliderEntry {

        GameObject owner;
        Collider collider;

        ColliderEntry(GameObject owner, Collider collider) {
            this.owner = owner;
            this.collider = collider;
        }
    }

    /**
     * Registers a game object and its collider with the collision world.
     *
     * @param owner the game object that owns the collider
     * @param collider the collider shape to associate with the owner
     */
    public void register(GameObject owner, Collider collider) {
        entries.add(new ColliderEntry(owner, collider));
    }

    /**
     * Removes the collider associated with the given game object.
     *
     * @param owner the game object to unregister
     */
    public void unregister(GameObject owner) {
        entries.removeIf(e -> e.owner == owner);
    }

    /**
     * Removes all registered colliders.
     */
    public void clear() {
        entries.clear();
    }

    /**
     * Tests all registered collider pairs and returns every overlapping pair.
     * Also removes entries whose owner has been destroyed.
     *
     * @return a list of CollisionResults; empty if no collisions occurred
     */
    public List<CollisionResult> checkCollisions() {
        List<CollisionResult> results = new ArrayList<>();

        List<ColliderEntry> snapshot = new ArrayList<>(entries);

        for (int i = 0; i < snapshot.size(); i++) {
            for (int j = i + 1; j < snapshot.size(); j++) {
                ColliderEntry a = snapshot.get(i);
                ColliderEntry b = snapshot.get(j);

                if (!a.owner.isAlive() || !b.owner.isAlive()) {
                    continue;
                }

                if (a.collider.intersects(b.collider)) {
                    boolean trigger = a.collider.isTrigger || b.collider.isTrigger;
                    results.add(new CollisionResult(a.owner, b.owner, trigger));
                }
            }
        }

        // Clean up dead entries after iteration, not during
        entries.removeIf(e -> !e.owner.isAlive());

        return results;
    }

    /**
     * Returns the collider associated with the given game object, or
     * {@code null} if the object is not registered.
     *
     * @param owner the game object to look up
     * @return the associated Collider, or {@code null}
     */
    public Collider getCollider(GameObject owner) {
        for (ColliderEntry e : entries) {
            if (e.owner == owner) {
                return e.collider;
            }
        }
        return null;
    }
}
