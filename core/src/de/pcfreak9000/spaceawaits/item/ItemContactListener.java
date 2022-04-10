package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;

public class ItemContactListener implements IContactListener {
    
    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            World world) {
        if (other.isEntity()) {
            Entity e = other.getEntity();
            if (Components.ITEM_STACK.has(e)) {
                ItemStackComponent me = Components.ITEM_STACK.get(owner.getEntity());
                ItemStackComponent you = Components.ITEM_STACK.get(e);
                me.stack = ItemStack.join(me.stack, you.stack);
                if (ItemStack.isEmptyOrNull(you.stack)) {
                    world.despawnEntity(e);
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean endContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            World world) {
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
