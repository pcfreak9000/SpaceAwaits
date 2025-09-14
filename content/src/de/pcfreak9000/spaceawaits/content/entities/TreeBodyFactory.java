package de.pcfreak9000.spaceawaits.content.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.b2BodyDef;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2Hull;
import com.badlogic.gdx.box2d.structs.b2Polygon;
import com.badlogic.gdx.box2d.structs.b2ShapeDef;
import com.badlogic.gdx.box2d.structs.b2Vec2;
import com.badlogic.gdx.box2d.structs.b2Vec2.b2Vec2Pointer;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.content.components.Components;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.ecs.IBodyFactory;

public class TreeBodyFactory implements IBodyFactory {
    
    private static final Vector2 WH = new Vector2(50 / 16f, 222 / 16f);
    
    private static final float[] bounds = UnitConversion.texelToPhysicsspace(METER_CONV, 16f, 222, new float[] { 12,
            221, /**/ 34, 221, /**/ 29, 165, /**/ 49, 156, /**/ 45, 14, /**/ 11, 1, /**/ 3, 136, /**/ 21, 165 });//<- not convex, but anyways
    
    
    @Override
    public b2BodyId createBody(b2WorldId world, Entity entity) {
    	b2BodyDef bd = Box2d.b2DefaultBodyDef();
        //BodyDef bd = new BodyDef();
        boolean loose = Components.TREESTATE.get(entity).loose;
        bd.fixedRotation(false);
        bd.type(loose ? b2BodyType.b2_dynamicBody : b2BodyType.b2_staticBody);
        bd.position().x(METER_CONV.in(0));
        bd.position().y(METER_CONV.in(0));
        b2ShapeDef fd = Box2d.b2DefaultShapeDef();
        //FixtureDef fd = new FixtureDef();
        //PolygonShape polyshape = new PolygonShape();
        b2Vec2Pointer p = new b2Vec2Pointer(bounds.length/2, true);
        for(int i=0; i<bounds.length/2; i++) {
        	b2Vec2 vec = new b2Vec2();
        	vec.x(bounds[i*2]);
        	vec.y(bounds[i*2+1]);
        }
        b2Hull hull = Box2d.b2ComputeHull(p, bounds.length/2);
        b2Polygon polyshape = Box2d.b2MakeBox(1, 1);//Box2d.b2MakePolygon(hull.asPointer(), 1);
        fd.density(10f);
        fd.isSensor(!loose);
        b2BodyId b = Box2d.b2CreateBody(world, bd.asPointer());
        Box2d.b2CreatePolygonShape(b, fd.asPointer(), polyshape.asPointer());
        return b;
    }
    
    @Override
    public Vector2 bodyOffset() {
        return Vector2.Zero;
    }
    
    @Override
    public Vector2 boundingBoxWidthAndHeight() {//This kinda sucks anyways
        return WH;//TODO Maybe change to a bounding sphere instead?
    }
    
}
