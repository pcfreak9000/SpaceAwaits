package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.WorldManifold;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;

public class SolidGroundContactListener implements IContactListener {
    
    private int count = 0;
    private float x, y;
    
    public void initializePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public boolean isOnSolidGround() {
        return count > 0;
    }
    
    public float getLastContactX() {
        return x;
    }
    
    public float getLastContactY() {
        return y;
    }
    
    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            World world) {
        if (!other.getFixture().isSensor() && contact.isTouching()) {
            count++;
        }
        return false;
    }
    
    @Override
    public boolean endContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            World world) {
        if (!other.getFixture().isSensor()) {
            count--;
            WorldManifold wm = contact.getWorldManifold();
            int n = wm.getNumberOfContactPoints();
            if (n > 0) {
                Vector2[] p = wm.getPoints();
                if (n == 1) {
                    x = p[0].x;
                    y = p[0].y;
                } else if (n == 2) {
                    x = p[0].x * 0.5f + p[1].x * 0.5f;
                    y = p[0].y * 0.5f + p[1].y * 0.5f;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean preSolve(UserDataHelper owner, UserDataHelper other, Contact contact, Manifold oldManifold,
            UnitConversion conv, World world) {
        return false;
    }
    
    @Override
    public boolean postSolve(UserDataHelper owner, UserDataHelper other, Contact contact, ContactImpulse impulse,
            UnitConversion conv, World world) {
        return false;
    }
    
}
