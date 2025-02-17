package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;

public class PlayerContactListener implements IContactListener {
    private SystemCache<EntityInteractSystem> eisys = new SystemCache<>(EntityInteractSystem.class);
    
    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            Engine world) {
        Player player = Components.PLAYER_INPUT.get(owner.getEntity()).player;
        if (other.isEntity()) {
            Entity ent = other.getEntity();
            if (Components.ITEM_STACK.has(ent)) {
                ItemStackComponent iscomp = Components.ITEM_STACK.get(ent);
                iscomp.stack = InvUtil.insert(player.getInventory(), iscomp.stack);
                if (iscomp.stack == null) {
                    eisys.get(world).despawnEntity(ent);
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean endContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            Engine world) {
        return false;
    }
    
    @Override
    public boolean preSolve(UserDataHelper owner, UserDataHelper other, Contact contact, Manifold oldManifold,
            UnitConversion conv, Engine world) {
        return false;
    }
    
    @Override
    public boolean postSolve(UserDataHelper owner, UserDataHelper other, Contact contact, ContactImpulse impulse,
            UnitConversion conv, Engine world) {
        return false;
    }
    
}
