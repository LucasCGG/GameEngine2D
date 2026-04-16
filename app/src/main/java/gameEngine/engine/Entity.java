package gameEngine.engine;

import gameEngine.attributes.Attributes;

public abstract class Entity extends GameObject {

    public Attributes attributes;

    public Entity(String name, Transform transform, Attributes attributes) {
        super(name, transform);
        this.attributes = attributes;
    }
}
