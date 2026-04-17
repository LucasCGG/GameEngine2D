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

    public void register(GameObject owner, Collider collider) {
        entries.add(new ColliderEntry(owner, collider));
    }

    public void unregister(GameObject owner) {
        entries.removeIf(e -> e.owner == owner);
    }

    public void clear() {
        entries.clear();
    }

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

    public Collider getCollider(GameObject owner) {
        for (ColliderEntry e : entries) {
            if (e.owner == owner) {
                return e.collider;
            }
        }
        return null;
    }
}
