package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class AABBBodyFactory implements BodyFactory {
    
    private final Vector2 offset;
    private final float width, height;
    private final float initx, inity;
    
    public AABBBodyFactory(float width, float height) {
        this(width, height, width / 2, height / 2, 0, 0);
    }
    
    public AABBBodyFactory(float width, float height, float xoffset, float yoffset) {
        this(width, height, xoffset, yoffset, 0, 0);
    }
    
    public AABBBodyFactory(float width, float height, float xoffset, float yoffset, float initx, float inity) {
        this.offset = new Vector2(xoffset, yoffset);
        this.width = width;
        this.height = height;
        this.initx = initx;
        this.inity = inity;
    }
    
    //What about a dynamic initial position?
    @Override
    public Body createBody(World world, UnitConversion meterconv) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;
        bd.type = BodyType.DynamicBody;
        bd.position.set(meterconv.in(initx), meterconv.in(inity));
        bd.position.add(meterconv.in(offset.y), meterconv.in(offset.x));
        FixtureDef fd = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(meterconv.in(width / 2), meterconv.in(height / 2));
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
