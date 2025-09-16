package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.b2BodyDef;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2Circle;
import com.badlogic.gdx.box2d.structs.b2ShapeDef;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.physics.ecs.IBodyFactory;

public class ItemBodyFactory implements IBodyFactory {
    private static final Vector2 OFFSET = new Vector2(Item.WORLD_SIZE / 2, Item.WORLD_SIZE / 2);
    private static final Vector2 WH = new Vector2(Item.WORLD_SIZE, Item.WORLD_SIZE);
    
    @Override
    public b2BodyId createBody(b2WorldId world, Entity entity) {
    	b2BodyDef bd = Box2d.b2DefaultBodyDef();
    	bd.fixedRotation(true);
        bd.type(b2BodyType.b2_dynamicBody);
        bd.position().x(METER_CONV.in(OFFSET.x));
        bd.position().y(METER_CONV.in(OFFSET.y));
        b2ShapeDef fd = Box2d.b2DefaultShapeDef();
        fd.enableContactEvents(true);
        fd.enableSensorEvents(true);
        b2Circle shape = new b2Circle();
        shape.radius(METER_CONV.in(Item.WORLD_SIZE / 1.9f));
        b2BodyId b = Box2d.b2CreateBody(world, bd.asPointer());
        Box2d.b2CreateCircleShape(b, fd.asPointer(), shape.asPointer());
        shape.radius(METER_CONV.in(Item.WORLD_SIZE / 1.1f));
        fd.isSensor(true);
        fd.enableSensorEvents(true);
        Box2d.b2CreateCircleShape(b, fd.asPointer(), shape.asPointer());
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
