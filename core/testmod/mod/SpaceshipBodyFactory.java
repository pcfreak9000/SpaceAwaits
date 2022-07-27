package mod;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.world.physics.BodyFactory;

public class SpaceshipBodyFactory implements BodyFactory {
    
    private static final Vector2 WH = new Vector2(159 / 32f, 79 / 32f);
    
    private static final Vector2[] bounds = { new Vector2(21, 75), new Vector2(158, 75), new Vector2(156f, 58f),
            new Vector2(107, 19), new Vector2(39, 0), new Vector2(1, 6), new Vector2(1, 63) };
    
    static {
        for (Vector2 v : bounds) {
            v.y = 79 - v.y;
            v.scl(1 / 32f);
            v.x = METER_CONV.in(v.x);
            v.y = METER_CONV.in(v.y);
        }
    }
    
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
