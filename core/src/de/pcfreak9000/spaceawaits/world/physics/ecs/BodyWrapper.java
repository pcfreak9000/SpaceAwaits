package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;

public class BodyWrapper {

    // If this class is reused for space as well this isn't suitable anymore
    private static final UnitConversion METER_CONV = PhysicsSystem.METER_CONV;

    private final Body body;

    public BodyWrapper(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public UnitConversion getMeterConv() {
        return METER_CONV;
    }

    public Vector2 getLinearVelocityW() {
        return METER_CONV.out(body.getLinearVelocity());
    }

    public Vector2 getLinearVelocityPh() {
        return body.getLinearVelocity();
    }

    public void applyAccelerationW(float ax, float ay) {
        this.body.applyForceToCenter(METER_CONV.in(ax) * this.body.getMass(), METER_CONV.in(ay) * this.body.getMass(),
                true);
    }

    public void applyAccelerationPh(float ax, float ay) {
        this.body.applyForceToCenter(ax * this.body.getMass(), ay * this.body.getMass(), true);
    }

    public void setLinearDampingPh(float d) {
        this.body.setLinearDamping(d);
    }

    public void setTransformW(float x, float y, float angle) {
        this.body.setTransform(METER_CONV.in(x), METER_CONV.in(y), angle);
    }

    public Vector2 getPositionW() {
        return METER_CONV.out(this.body.getPosition());
    }

    public Vector2 getWorldCenterW() {
        return METER_CONV.out(this.body.getWorldCenter());
    }

    public Vector2 getLocalCenterW() {
        return METER_CONV.out(this.body.getLocalCenter());
    }

    public float getAngle() {
        return this.body.getAngle();
    }

    public float getRotation() {
        return this.body.getTransform().getRotation();
    }

    public void setVelocityW(float vx, float vy) {
        this.body.setLinearVelocity(METER_CONV.in(vx), METER_CONV.in(vy));
    }

}
