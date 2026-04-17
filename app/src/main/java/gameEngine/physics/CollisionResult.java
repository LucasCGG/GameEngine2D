package gameEngine.physics;

import gameEngine.core.GameObject;

public class CollisionResult {

    public final GameObject a;
    public final GameObject b;
    public final boolean isTrigger;

    public CollisionResult(GameObject a, GameObject b, boolean isTrigger) {
        this.a = a;
        this.b = b;
        this.isTrigger = isTrigger;
    }
}
