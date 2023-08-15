package de.pcfreak9000.spaceawaits.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.core.SolidGroundContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UserData;
import de.pcfreak9000.spaceawaits.world.physics.ecs.IBodyFactory;

public class PlayerBodyFactory implements IBodyFactory {
    
    private final Vector2 OFFSET;
    private final Vector2 WH;
    private final SolidGroundContactListener l;
    
    public PlayerBodyFactory(float w, float h, SolidGroundContactListener l) {
        this.OFFSET = new Vector2(w / 2, h / 2 * 0.9f);
        this.WH = new Vector2(w, h);
        this.l = l;
    }
    
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;
        bd.type = BodyType.DynamicBody;
        bd.position.set(METER_CONV.in(OFFSET.x), METER_CONV.in(OFFSET.x));
        FixtureDef fd = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(METER_CONV.in(WH.y / 5.5f));
        shape.setPosition(METER_CONV.in(new Vector2(0, WH.y / 3.6f)));
        fd.shape = shape;
        fd.friction = 0;
        fd.density = 1.1f;
        Body b = world.createBody(bd);
        b.createFixture(fd);
        shape.setPosition(METER_CONV.in(new Vector2(0, -WH.y / 4)));
        b.createFixture(fd);
        shape.setPosition(METER_CONV.in(new Vector2(0, 0)));
        b.createFixture(fd);
        PolygonShape psh = new PolygonShape();
        psh.setAsBox(METER_CONV.in(WH.x * 0.015f), METER_CONV.in(WH.y / 16),
                METER_CONV.in(new Vector2(0, -WH.y / 2 + 3*WH.y / 32f)), 0);
        fd.shape = psh;
        fd.isSensor = true;
        Fixture f = b.createFixture(fd);
        UserData ud = new UserData();
        ud.listener = l;
        f.setUserData(ud);
        psh.dispose();
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
