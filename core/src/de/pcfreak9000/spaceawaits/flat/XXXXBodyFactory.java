package de.pcfreak9000.spaceawaits.flat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.world.physics.ecs.IBodyFactory;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class XXXXBodyFactory implements IBodyFactory {
    private static final Vector2 OFFSET = new Vector2(Item.WORLD_SIZE / 2, Item.WORLD_SIZE / 2);
    private static final Vector2 WH = new Vector2(Item.WORLD_SIZE, Item.WORLD_SIZE);
    
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = false;
        bd.type = BodyType.DynamicBody;
        bd.position.set(METER_CONV.in(OFFSET.x), METER_CONV.in(OFFSET.x));
        FixtureDef fd = new FixtureDef();
        fd.friction = 0;
        fd.density = 0.001f;
        CircleShape shape = new CircleShape();
        shape.setRadius(METER_CONV.in(Item.WORLD_SIZE / 5f));
        fd.shape = shape;
        Body b = world.createBody(bd);
        b.createFixture(fd);
        shape.dispose();
        return b;
    }
    
    @Override
    public Vector2 boundingBoxWidthAndHeight() {
        return WH;
    }
    
    @Override
    public Vector2 bodyOffset() {
        return OFFSET;
    }
    
}
