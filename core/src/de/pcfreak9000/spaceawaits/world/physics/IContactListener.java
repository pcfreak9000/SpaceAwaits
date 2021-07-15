package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.world.World;

public interface IContactListener {
    
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv, World world);
    
    public boolean endContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv, World world);
    
    public boolean preSolve(UserDataHelper owner, UserDataHelper other, Contact contact, Manifold oldManifold, UnitConversion conv,
            World world);
    
    public boolean postSolve(UserDataHelper owner, UserDataHelper other, Contact contact, ContactImpulse impulse, UnitConversion conv,
            World world);
}
