package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class AABBBodyFactory implements BodyFactory {
    
    //TODO fix this class
    
    private final Vector2 offset;
    private final float width, height;
    private final float initx, inity;
    private final BodyType t;

    public AABBBodyFactory(float width, float height) {
        this(width, height, width / 2, height / 2, 0, 0, BodyType.DynamicBody);
    }
    
    public AABBBodyFactory(float width, float height, float xoffset, float yoffset) {
        this(width, height, xoffset, yoffset, 0, 0, BodyType.DynamicBody);
    }
    
    public static AABBBodyFactory create(float width, float height, float initx, float inity) {
        return new AABBBodyFactory(width, height, width / 2, height / 2, initx, inity, BodyType.StaticBody);
    }

    public AABBBodyFactory(float width, float height, float xoffset, float yoffset, float initx, float inity,
            BodyType type) {
        this.offset = new Vector2(xoffset, yoffset);
        this.width = width;
        this.height = height;
        this.initx = initx;
        this.inity = inity;
        this.t = type;
    }
    
    //What about a dynamic initial position?
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;
        bd.type = t;
        bd.position.set(METER_CONV.in(initx), METER_CONV.in(inity));
        bd.position.add(METER_CONV.in(offset.x), METER_CONV.in(offset.y));
        FixtureDef fd = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(METER_CONV.in(width / 2), METER_CONV.in(height / 2));
        fd.shape = shape;
        Body b = world.createBody(bd);
        b.createFixture(fd);//PhysicsComponent userdata?
        shape.dispose();
        return b;
    }
    
    @Override
    public Vector2 bodyOffset() {
        return offset;
    }
    
}
