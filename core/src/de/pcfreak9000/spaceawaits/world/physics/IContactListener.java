package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.world.World;

public interface IContactListener {
    
    public void beginContact(UserData owner, UserData other, Contact contact, UnitConversion conv, World world);
    
    public void endContact(UserData owner, UserData other, Contact contact, UnitConversion conv, World world);
    
    public void preSolve(UserData owner, UserData other, Contact contact, Manifold oldManifold, UnitConversion conv,
            World world);
    
    public void postSolve(UserData owner, UserData other, Contact contact, ContactImpulse impulse, UnitConversion conv,
            World world);
}
