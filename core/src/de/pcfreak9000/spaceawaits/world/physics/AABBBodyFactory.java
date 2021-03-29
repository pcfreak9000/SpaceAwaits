package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class AABBBodyFactory implements BodyFactory {
    
    public static Builder builder() {
        return new Builder();
    }
    
    private final Vector2 offset;
    private final float width, height;
    private final float initx, inity;
    private final BodyType t;
    
    public AABBBodyFactory(float width, float height, float xoffset, float yoffset, float initx, float inity,
            BodyType type) {
        this.offset = new Vector2(xoffset, yoffset);
        this.width = width;
        this.height = height;
        this.initx = initx;
        this.inity = inity;
        this.t = type;
    }
    
    //What about a dynamic initial position?
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;
        bd.type = t;
        bd.position.set(METER_CONV.in(initx), METER_CONV.in(inity));
        bd.position.add(METER_CONV.in(offset.x), METER_CONV.in(offset.y));
        FixtureDef fd = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(METER_CONV.in(width / 2), METER_CONV.in(height / 2));
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
    
    public static final class Builder {
        
        private float w = -1, h = -1, ix, iy, ox = -1, oy = -1;
        private BodyType type;
        
        public Builder dimensions(float width, float height) {
            w = width;
            h = height;
            return this;
        }
        
        public Builder initialPosition(float x, float y) {
            ix = x;
            iy = y;
            return this;
        }
        
        public Builder offsets(float ox, float oy) {
            this.ox = ox;
            this.oy = oy;
            return this;
        }
        
        //Default
        public Builder dynamicBody() {
            this.type = BodyType.DynamicBody;
            return this;
        }
        
        public Builder kinematicBody() {
            this.type = BodyType.KinematicBody;
            return this;
        }
        
        public Builder staticBody() {
            this.type = BodyType.StaticBody;
            return this;
        }
        
        //Default
        public Builder offsetsFromDimensions() {
            if (w == -1 || h == -1) {
                throw new IllegalStateException("Dimensions haven't been specified yet");
            }
            ox = w / 2;
            oy = h / 2;
            return this;
        }
        
        public AABBBodyFactory create() {
            if (w == -1 || h == -1) {
                dimensions(0, 0);
            }
            if (ox == -1 || oy == -1) {
                offsetsFromDimensions();
            }
            if (type == null) {
                dynamicBody();
            }
            return new AABBBodyFactory(w, h, ox, oy, ix, iy, type);
        }
    }
}
