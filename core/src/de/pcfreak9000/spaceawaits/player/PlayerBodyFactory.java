package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.b2BodyDef;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2Circle;
import com.badlogic.gdx.box2d.structs.b2Polygon;
import com.badlogic.gdx.box2d.structs.b2Rot;
import com.badlogic.gdx.box2d.structs.b2ShapeDef;
import com.badlogic.gdx.box2d.structs.b2ShapeId;
import com.badlogic.gdx.box2d.structs.b2SurfaceMaterial;
import com.badlogic.gdx.box2d.structs.b2Vec2;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.physics.IDFactory;
import de.pcfreak9000.spaceawaits.world.physics.SolidGroundContactListener;
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
	public b2BodyId createBody(b2WorldId world, Entity entity) {
		b2BodyDef bd = Box2d.b2DefaultBodyDef();
		bd.fixedRotation(true);
		bd.linearDamping(0.01f);
		bd.type(b2BodyType.b2_dynamicBody);
		bd.position().x(METER_CONV.in(OFFSET.x));
		bd.position().y(METER_CONV.in(OFFSET.y));
		b2ShapeDef fd = Box2d.b2DefaultShapeDef();
		b2Circle shape = new b2Circle();
		shape.radius(METER_CONV.in(WH.y / 5.5f));
		shape.center().x(METER_CONV.in(0));
		shape.center().y(METER_CONV.in(WH.y / 3.6f));

		b2SurfaceMaterial mat = fd.material();
		mat.friction(15f);
		fd.density(1.1f);
		fd.enableSensorEvents(true);
		fd.enableContactEvents(true);
		b2BodyId b = Box2d.b2CreateBody(world, bd.asPointer());
		Box2d.b2CreateCircleShape(b, fd.asPointer(), shape.asPointer());
		shape.center().y(METER_CONV.in(-WH.y / 4f));
		Box2d.b2CreateCircleShape(b, fd.asPointer(), shape.asPointer());
		shape.center().y(METER_CONV.in(0));
		Box2d.b2CreateCircleShape(b, fd.asPointer(), shape.asPointer());
		// PolygonShape psh = new PolygonShape();
		b2Rot rot = Box2d.b2MakeRot(0);
		rot.c(1);
		rot.s(0);
		b2Vec2 cen = new b2Vec2();
		cen.y(METER_CONV.in(-WH.y / 2 + 5 * WH.y / 64f));
		b2Polygon poly = Box2d.b2MakeOffsetBox(METER_CONV.in(WH.x * 0.3f), METER_CONV.in(WH.y / 32f), cen, rot);
		fd.isSensor(true);
		fd.enableSensorEvents(true);
		b2ShapeId sid = Box2d.b2CreatePolygonShape(b, fd.asPointer(), poly.asPointer());
		UserData ud = new UserData();
		ud.listener = l;
		Box2d.b2Shape_SetUserData(sid, IDFactory.putData(ud));
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
