package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.b2BodyDef;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2Polygon;
import com.badlogic.gdx.box2d.structs.b2ShapeDef;
import com.badlogic.gdx.box2d.structs.b2Vec2;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Vector2;

public class AABBBodyFactory implements IBodyFactory {
    
    public static Builder builder() {
        return new Builder();
    }
    
    private final Vector2 offset;
    private final Vector2 widthAndHeight;
    private final float initx, inity;
    private final b2BodyType t;
    
    public AABBBodyFactory(float width, float height, float xoffset, float yoffset, float initx, float inity,
            b2BodyType type) {
        this.offset = new Vector2(xoffset, yoffset);
        this.widthAndHeight = new Vector2(width, height);
        this.initx = initx;
        this.inity = inity;
        this.t = type;
    }
    
    //What about a dynamic initial position?
    @Override
    public b2BodyId createBody(b2WorldId world, Entity entity) {
        b2BodyDef bd = Box2d.b2DefaultBodyDef();
        bd.fixedRotation(true);
        bd.type(t);
        b2Vec2 pos = bd.position();
        pos.x(METER_CONV.in(offset.x+initx));
        pos.y(METER_CONV.in(offset.y+inity));
        b2ShapeDef fd = Box2d.b2DefaultShapeDef();
        b2Polygon poly = Box2d.b2MakeBox(METER_CONV.in(widthAndHeight.x / 2), METER_CONV.in(widthAndHeight.y / 2));
        b2BodyId b = Box2d.b2CreateBody(world, bd.asPointer());
        Box2d.b2CreatePolygonShape(b, fd.asPointer(), poly.asPointer());//PhysicsComponent userdata?
        return b;
    }
    
    @Override
    public Vector2 boundingBoxWidthAndHeight() {
        return widthAndHeight;
    }
    
    @Override
    public Vector2 bodyOffset() {
        return offset;
    }
    
    public static final class Builder {
        
        private float w = -1, h = -1, ix, iy, ox = -1, oy = -1;
        private b2BodyType type;
        
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
            this.type = b2BodyType.b2_dynamicBody;
            return this;
        }
        
        public Builder kinematicBody() {
            this.type = b2BodyType.b2_kinematicBody;
            return this;
        }
        
        public Builder staticBody() {
            this.type = b2BodyType.b2_staticBody;
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
