package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

class ContactListenerImpl implements ContactListener {
    
    private static final ComponentMapper<ContactListenerComponent> CONTACT_LISTENER_COMP_MAPPER = ComponentMapper
            .getFor(ContactListenerComponent.class);
    
    private UserData userdata1 = new UserData();
    private UserData userdata2 = new UserData();
    
    private IContactListener getListener(UserData in) {
        if (in.isTile()) {
            return in.getTile().getContactListener();
        } else if (in.isEntity()) {
            ContactListenerComponent comp = CONTACT_LISTENER_COMP_MAPPER.get(in.getEntity());
            if (comp != null) {
                return CONTACT_LISTENER_COMP_MAPPER.get(in.getEntity()).listener;
            }
        }
        return null;
    }
    
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData());
        userdata2.set(f2.getUserData());
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        if (l1 != null) {
            l1.preSolve(userdata1, userdata2, contact, oldManifold);
        }
        if (l2 != null) {
            l2.preSolve(userdata2, userdata1, contact, oldManifold);
        }
    }
    
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData());
        userdata2.set(f2.getUserData());
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        if (l1 != null) {
            l1.postSolve(userdata1, userdata2, contact, impulse);
        }
        if (l2 != null) {
            l2.postSolve(userdata2, userdata1, contact, impulse);
        }
    }
    
    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData());
        userdata2.set(f2.getUserData());
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        if (l1 != null) {
            l1.beginContact(userdata1, userdata2, contact);
        }
        if (l2 != null) {
            l2.beginContact(userdata2, userdata1, contact);
        }
    }
    
    @Override
    public void endContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData());
        userdata2.set(f2.getUserData());
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        if (l1 != null) {
            l1.endContact(userdata1, userdata2, contact);
        }
        if (l2 != null) {
            l2.endContact(userdata2, userdata1, contact);
        }
    }
    
}
