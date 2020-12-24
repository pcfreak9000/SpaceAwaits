package de.pcfreak9000.spaceawaits.tileworld.ecs;

import java.util.BitSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PhysicsComponent implements Component {
    
    public final Vector2 velocity = new Vector2();
    public final Vector2 acceleration = new Vector2();
    
    public boolean onGround = false;
    
    public float x, y, w, h;
    public float restitution = 0;
    
    private BitSet flags = new BitSet();
    
    public void setFlags(int index, boolean collision, boolean resolution) {
        flags.set(index * 2, collision);
        flags.set(index * 2 + 1, resolution);
    }
    
    public boolean collide(int index) {
        return flags.get(index * 2);
    }
    
    public boolean resolve(int index) {
        return flags.get(index * 2 + 1);
    }
    
}
