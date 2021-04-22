package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public interface IContactListener {
    
    public void beginContact(UserData owner, UserData other, Contact contact);
    
    public void endContact(UserData owner, UserData other, Contact contact);
    
    public void preSolve(UserData owner, UserData other, Contact contact, Manifold oldManifold);
    
    public void postSolve(UserData owner, UserData other, Contact contact, ContactImpulse impulse);
}
