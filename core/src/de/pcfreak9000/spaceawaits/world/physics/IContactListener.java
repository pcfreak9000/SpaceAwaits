package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public interface IContactListener {
    
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            Engine world);
    
    public boolean endContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            Engine world);
    
    public boolean preSolve(UserDataHelper owner, UserDataHelper other, Contact contact, Manifold oldManifold,
            UnitConversion conv, Engine world);
    
    public boolean postSolve(UserDataHelper owner, UserDataHelper other, Contact contact, ContactImpulse impulse,
            UnitConversion conv, Engine world);
}
