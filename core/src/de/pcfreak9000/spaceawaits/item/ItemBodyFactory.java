package de.pcfreak9000.spaceawaits.item;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.world.physics.BodyFactory;

public class ItemBodyFactory implements BodyFactory {
    private static final Vector2 OFFSET = new Vector2(Item.WORLD_SIZE / 2, Item.WORLD_SIZE / 2);
    
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;
        bd.type = BodyType.DynamicBody;
        bd.position.set(METER_CONV.in(OFFSET.x), METER_CONV.in(OFFSET.x));
        FixtureDef fd = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Item.WORLD_SIZE / 2f);
        fd.shape = shape;
        Body b = world.createBody(bd);
        b.createFixture(fd);
        shape.setRadius(Item.WORLD_SIZE / 1.4f);
        fd.isSensor = true;
        b.createFixture(fd);
        shape.dispose();
        return b;
    }
    
    @Override
    public Vector2 bodyOffset() {
        return OFFSET;
    }
    
}
