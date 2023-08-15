package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserData;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;

class ContactListenerImpl implements ContactListener {
    
    private UserDataHelper userdata1 = new UserDataHelper();
    private UserDataHelper userdata2 = new UserDataHelper();
    private final World world;
    private final UnitConversion conv;
    
    public ContactListenerImpl(World world, UnitConversion conv) {
        this.world = world;
        this.conv = conv;
    }
    
    private IContactListener getListener(UserDataHelper in) {
        if (in.isTile()) {
            return in.getTile().getContactListener();
        } else if (in.isEntity()) {
            ContactListenerComponent comp = Components.CONTACT_LISTENER.get(in.getEntity());
            if (comp != null) {
                return Components.CONTACT_LISTENER.get(in.getEntity()).listener;
            }
        } else if (in.isUDCustom()) {
            UserData ud = in.getUDCustom();
            return ud.listener;
        }
        return null;
    }
    
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData(), f1);
        userdata2.set(f2.getUserData(), f2);
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        boolean b = false;
        if (l1 != null) {
            b = l1.preSolve(userdata1, userdata2, contact, oldManifold, conv, world);
        }
        if (!b && l2 != null) {
            l2.preSolve(userdata2, userdata1, contact, oldManifold, conv, world);
        }
    }
    
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData(), f1);
        userdata2.set(f2.getUserData(), f2);
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        boolean b = false;
        if (l1 != null) {
            b = l1.postSolve(userdata1, userdata2, contact, impulse, conv, world);
        }
        if (!b && l2 != null) {
            l2.postSolve(userdata2, userdata1, contact, impulse, conv, world);
        }
    }
    
    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData(), f1);
        userdata2.set(f2.getUserData(), f2);
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        boolean b = false;
        if (l1 != null) {
            b = l1.beginContact(userdata1, userdata2, contact, conv, world);
        }
        if (!b && l2 != null) {
            l2.beginContact(userdata2, userdata1, contact, conv, world);
        }
    }
    
    @Override
    public void endContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();
        userdata1.set(f1.getUserData(), f1);
        userdata2.set(f2.getUserData(), f2);
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        boolean b = false;
        if (l1 != null) {
            b = l1.endContact(userdata1, userdata2, contact, conv, world);
        }
        if (!b && l2 != null) {
            l2.endContact(userdata2, userdata1, contact, conv, world);
        }
    }
    
}
