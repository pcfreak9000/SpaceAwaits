package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.WorldManifold;

import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;

public class SolidGroundContactListener implements IContactListener {
    
    private OnSolidGroundComponent backingComp;
    
    public SolidGroundContactListener(OnSolidGroundComponent comp) {
        this.backingComp = comp;
    }
    
    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            Engine world) {
        if (!other.getFixture().isSensor() && contact.isTouching()) {
            backingComp.solidGroundContacts++;
        }
        return false;
    }
    
    @Override
    public boolean endContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            Engine world) {
        if (!other.getFixture().isSensor()) {
            backingComp.solidGroundContacts--;
            if (backingComp.solidGroundContacts < 0) {
                backingComp.solidGroundContacts = 0;
            }
            WorldManifold wm = contact.getWorldManifold();
            int n = wm.getNumberOfContactPoints();
            if (n > 0) {
                Vector2[] p = wm.getPoints();
                if (n == 1) {
                    backingComp.lastContactX = p[0].x;
                    backingComp.lastContactY = p[0].y;
                } else if (n == 2) {
                    backingComp.lastContactX = p[0].x * 0.5f + p[1].x * 0.5f;
                    backingComp.lastContactY = p[0].y * 0.5f + p[1].y * 0.5f;
                }//FIXME ? this looks like it needs a METER_CONV conversion?
            }
        }
        return false;
    }
    
    @Override
    public boolean preSolve(UserDataHelper owner, UserDataHelper other, Contact contact, Manifold oldManifold,
            UnitConversion conv, Engine world) {
        return false;
    }
    
    @Override
    public boolean postSolve(UserDataHelper owner, UserDataHelper other, Contact contact, ContactImpulse impulse,
            UnitConversion conv, Engine world) {
        return false;
    }
    
}
