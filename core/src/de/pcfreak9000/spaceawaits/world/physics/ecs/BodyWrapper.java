package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2Rot;
import com.badlogic.gdx.box2d.structs.b2Transform;
import com.badlogic.gdx.box2d.structs.b2Vec2;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;

public class BodyWrapper {

	// If this class is reused for space as well this isn't suitable anymore
	private static final UnitConversion METER_CONV = PhysicsSystem.METER_CONV;

	private final b2BodyId body;

	public BodyWrapper(b2BodyId body) {
		this.body = body;
	}

	public b2BodyId getBody() {
		return body;
	}

	public UnitConversion getMeterConv() {
		return METER_CONV;
	}

	public Vector2 getLinearVelocityW() {
		b2Vec2 b2 = Box2d.b2Body_GetLinearVelocity(body);
		return METER_CONV.out(new Vector2(b2.x(), b2.y()));
	}

	public Vector2 getLinearVelocityPh() {
		b2Vec2 b2 = Box2d.b2Body_GetLinearVelocity(body);
		return new Vector2(b2.x(), b2.y());
	}

	public void applyAccelerationW(float ax, float ay) {
		b2Vec2 b2 = new b2Vec2();
		float mass = Box2d.b2Body_GetMass(body);
		b2.x(METER_CONV.in(ax) * mass);
		b2.y(METER_CONV.in(ay) * mass);
		Box2d.b2Body_ApplyForceToCenter(body, b2, true);
	}

	public void applyAccelerationPh(float ax, float ay) {
		b2Vec2 b2 = new b2Vec2();
		float mass = Box2d.b2Body_GetMass(body);
		b2.x(ax * mass);
		b2.y(ay * mass);
		Box2d.b2Body_ApplyForceToCenter(body, b2, true);
	}

	public void setLinearDampingPh(float d) {
		Box2d.b2Body_SetLinearDamping(body, d);
	}

	public void setTransformW(float x, float y, float angle) {
		b2Rot rot = Box2d.b2MakeRot(angle);
		b2Vec2 pos = new b2Vec2();
		pos.x(METER_CONV.in(x));
		pos.y(METER_CONV.in(y));
		Box2d.b2Body_SetTransform(body, pos, rot);
	}

	public Vector2 getPositionW() {
		b2Vec2 pos = Box2d.b2Body_GetPosition(body);
		return METER_CONV.out(new Vector2(pos.x(), pos.y()));
	}

	public Vector2 getWorldCenterW() {
		b2Vec2 c = Box2d.b2Body_GetWorldCenterOfMass(body);
		return METER_CONV.out(new Vector2(c.x(), c.y()));
	}

	public Vector2 getLocalCenterW() {
		b2Vec2 c = Box2d.b2Body_GetLocalCenterOfMass(body);
		return METER_CONV.out(new Vector2(c.x(), c.y()));
	}

	public float getAngle() {
		b2Rot r = Box2d.b2Body_GetRotation(body);
		float a = Box2d.b2Rot_GetAngle(r);
		return a;
	}

	public float getRotation() {
		b2Transform t = Box2d.b2Body_GetTransform(body);
		b2Rot r = t.getQ();
		float a = Box2d.b2Rot_GetAngle(r);
		return a;
	}

	public void setVelocityW(float vx, float vy) {
		b2Vec2 vel = new b2Vec2();
		vel.x(METER_CONV.in(vx));
		vel.y(METER_CONV.in(vy));
		Box2d.b2Body_SetLinearVelocity(body, vel);
	}

}
