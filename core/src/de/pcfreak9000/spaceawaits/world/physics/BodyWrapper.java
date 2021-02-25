package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class BodyWrapper {
    
    private final Body body;
    private final UnitConversion meterconv;
    
    public BodyWrapper(Body body, UnitConversion meterconv) {
        this.body = body;
        this.meterconv = meterconv;
    }
    
    public Body getBody() {
        return body;
    }
    
    public UnitConversion getMeterConv() {
        return meterconv;
    }
    
    public Vector2 getLinearVelocityW() {
        return meterconv.out(body.getLinearVelocity());
    }
    
    public Vector2 getLinearVelocityPh() {
        return body.getLinearVelocity();
    }
    
    public void applyAccelerationW(float ax, float ay) {
        this.body.applyForceToCenter(this.meterconv.in(ax) * this.body.getMass(),
                this.meterconv.in(ay) * this.body.getMass(), true);
    }
    
    public void applyAccelerationPh(float ax, float ay) {
        this.body.applyForceToCenter(ax * this.body.getMass(), ay * this.body.getMass(), true);
    }
    
    public void setTransformW(float x, float y, float angle) {
        this.body.setTransform(meterconv.in(x), meterconv.in(y), angle);
    }
    
    public Vector2 getPositionW() {
        return meterconv.out(this.body.getPosition());
    }
    
}
