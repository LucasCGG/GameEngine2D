package gameEngine.entitys;

import gameEngine.attributes.Attributes;
import gameEngine.core.GameObject;
import gameEngine.core.Transform;
import gameEngine.sprites.Sprite;

public abstract class Entity extends GameObject {

    public Attributes attributes;

    public Entity(String name, Transform transform, Attributes attributes) {
        super(name, transform);
        this.attributes = attributes;
    }

    public abstract Sprite getSprite();
}
