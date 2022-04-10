package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;

public class PlayerContactListener implements IContactListener {
    
    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            World world) {
        Player player = Components.PLAYER_INPUT.get(owner.getEntity()).player;
        if (other.isEntity()) {
            Entity ent = other.getEntity();
            if (Components.ITEM_STACK.has(ent)) {
                ItemStackComponent iscomp = Components.ITEM_STACK.get(ent);
                iscomp.stack = InvUtil.insert(player.getInventory(), iscomp.stack);
                if (iscomp.stack == null) {
                    world.despawnEntity(ent);
                }
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
