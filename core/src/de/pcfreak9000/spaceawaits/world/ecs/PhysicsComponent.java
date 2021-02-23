package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsComponent implements Component {
    
    public Body body;
    
    @Deprecated
    public final Vector2 velocity = new Vector2();
    
    public final Vector2 acceleration = new Vector2();
    
    public boolean onGround = false;
    public int somecount = 0;
    
    public float x, y, w, h;
    
}
