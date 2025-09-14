package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.structs.b2Manifold;
import com.badlogic.gdx.box2d.structs.b2ShapeId;

import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserData;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;

class ContactListenerImpl {
    
    private UserDataHelper userdata1 = new UserDataHelper();
    private UserDataHelper userdata2 = new UserDataHelper();
    private Engine world;
    private final UnitConversion conv;
    
    public ContactListenerImpl(UnitConversion conv) {
        this.conv = conv;
    }
    
    public void setEngine(Engine engine) {
        this.world = engine;
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
    
    public void beginContact(b2ShapeId f1, b2ShapeId f2, b2Manifold manifold) {
        userdata1.set(Box2d.b2Shape_GetUserData(f1), f1);
        userdata2.set(Box2d.b2Shape_GetUserData(f2), f2);
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        boolean b = false;
        if (l1 != null) {
            b = l1.beginContact(userdata1, userdata2, manifold, conv, world);
        }
        if (!b && l2 != null) {
            l2.beginContact(userdata2, userdata1, manifold, conv, world);
        }
    }
    
    public void endContact(b2ShapeId f1, b2ShapeId f2) {
        userdata1.set(Box2d.b2Shape_GetUserData(f1), f1);
        userdata2.set(Box2d.b2Shape_GetUserData(f2), f2);
        IContactListener l1 = getListener(userdata1);
        IContactListener l2 = getListener(userdata2);
        boolean b = false;
        if (l1 != null) {
            b = l1.endContact(userdata1, userdata2, conv, world);
        }
        if (!b && l2 != null) {
            l2.endContact(userdata2, userdata1, conv, world);
        }
    }
    
}
