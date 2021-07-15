package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.world.physics.BodyFactory;

public class PlayerBodyFactory implements BodyFactory {
    
    private final Vector2 OFFSET;
    private final float w;
    private final float h;
    
    public PlayerBodyFactory(float w, float h) {
        this.OFFSET = new Vector2(w / 2, h / 2 * 0.9f);
        this.w = w;
        this.h = h;
    }
    
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;
        bd.type = BodyType.DynamicBody;
        bd.position.set(METER_CONV.in(OFFSET.x), METER_CONV.in(OFFSET.x));
        FixtureDef fd = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(METER_CONV.in(h / 4));
        shape.setPosition(METER_CONV.in(new Vector2(0, h / 4 * 0.95f)));
        fd.shape = shape;
        Body b = world.createBody(bd);
        b.createFixture(fd);
        shape.setPosition(METER_CONV.in(new Vector2(0, -h / 4)));
        b.createFixture(fd);
        shape.dispose();
        return b;
    }
    
    @Override
    public Vector2 bodyOffset() {
        return OFFSET;
    }
    
}
