package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.structs.b2Manifold;
import com.badlogic.gdx.box2d.structs.b2Vec2;

import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;

public class SolidGroundContactListener implements IContactListener {

    private OnSolidGroundComponent backingComp;

    public SolidGroundContactListener(OnSolidGroundComponent comp) {
        this.backingComp = comp;
    }

    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, b2Manifold manifold, UnitConversion conv,
            Engine world) {
        if (!Box2d.b2Shape_IsSensor(other.getFixture())) {
            backingComp.solidGroundContacts++;
            int n = 0;//manifold.pointCount();
            if (n > 0) {
                //not sure if this works properly, i.e. if anchora is really what it is assumed to be
            	//anyways, we need to record the position anyways in backingcomp 
                if (n == 1) {
                	b2Vec2 p = manifold.getPoints().get(0).anchorA();
                    backingComp.lastContactX = p.x();
                    backingComp.lastContactY = p.y();
                } else if (n == 2) {
                	b2Vec2 p0 = manifold.getPoints().get(0).anchorA();
                	b2Vec2 p1 = manifold.getPoints().get(1).anchorA();
                    backingComp.lastContactX = p0.x() * 0.5f + p1.x() * 0.5f;
                    backingComp.lastContactY = p0.y() * 0.5f + p1.y() * 0.5f;
                } // FIXME ? this looks like it needs a METER_CONV conversion?
            }
        }
        return false;
    }

    @Override
    public boolean endContact(UserDataHelper owner, UserDataHelper other, UnitConversion conv,
            Engine world) {
        if (!Box2d.b2Shape_IsSensor(other.getFixture())) {
            backingComp.solidGroundContacts--;
            if (backingComp.solidGroundContacts < 0) {
                backingComp.solidGroundContacts = 0;
            }
        }
        return false;
    }


}
