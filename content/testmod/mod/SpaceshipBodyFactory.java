package mod;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.ecs.IBodyFactory;

public class SpaceshipBodyFactory implements IBodyFactory {
    
    private static final Vector2 WH = new Vector2(159 / 32f, 72 / 32f);
    
    private static final float[] bounds = UnitConversion.texelToPhysicsspace(METER_CONV, 32f, 72,
            new float[] { 21, 72, /**/ 158, 72, /**/ 156f, 58f, /**/107, 19, /**/39, 0, /**/1, 6, /**/1, 63 });
    
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;
        bd.type = BodyType.DynamicBody;
        bd.position.set(METER_CONV.in(0), METER_CONV.in(0));
        FixtureDef fd = new FixtureDef();
        PolygonShape polyshape = new PolygonShape();
        polyshape.set(bounds);
        fd.shape = polyshape;
        fd.density = 1000;
        Body b = world.createBody(bd);
        b.createFixture(fd);
        return b;
    }
    
    @Override
    public Vector2 bodyOffset() {
        return Vector2.Zero;
    }
    
    @Override
    public Vector2 boundingBoxWidthAndHeight() {
        return WH;
    }
    
}
