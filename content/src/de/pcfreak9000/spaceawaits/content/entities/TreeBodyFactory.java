package de.pcfreak9000.spaceawaits.content.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.content.components.Components;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.ecs.IBodyFactory;

public class TreeBodyFactory implements IBodyFactory {
    
    private static final Vector2 WH = new Vector2(50 / 16f, 222 / 16f);
    
    private static final float[] bounds = UnitConversion.texelToPhysicsspace(METER_CONV, 16f, 222, new float[] { 12,
            221, /**/ 34, 221, /**/ 29, 165, /**/ 49, 156, /**/ 45, 14, /**/ 11, 1, /**/ 3, 136, /**/ 21, 165 });//<- not convex, but anyways
    
    //this isnt used
    @Override
    public Body createBody(World world) {
        return null;
    }
    
    @Override
    public Body createBody(World world, Entity entity) {
        BodyDef bd = new BodyDef();
        boolean loose = Components.TREESTATE.get(entity).loose;
        bd.fixedRotation = false;
        bd.type = loose ? BodyType.DynamicBody : BodyType.StaticBody;
        bd.position.set(METER_CONV.in(0), METER_CONV.in(0));
        FixtureDef fd = new FixtureDef();
        PolygonShape polyshape = new PolygonShape();
        polyshape.set(bounds);
        fd.shape = polyshape;
        fd.density = 10f;
        fd.isSensor = !loose;
        Body b = world.createBody(bd);
        b.createFixture(fd);
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
