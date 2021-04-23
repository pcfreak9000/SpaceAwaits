package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UserData;

public class PlayerContactListener implements IContactListener {
    
    private static final ComponentMapper<PlayerInputComponent> PLAYER_COMP_MAPPER = ComponentMapper
            .getFor(PlayerInputComponent.class);
    
    private static final ComponentMapper<ItemStackComponent> ITEM_STACK_COMP_MAPPER = ComponentMapper
            .getFor(ItemStackComponent.class);
    
    @Override
    public void beginContact(UserData owner, UserData other, Contact contact) {
    }
    
    @Override
    public void endContact(UserData owner, UserData other, Contact contact) {
    }
    
    @Override
    public void preSolve(UserData owner, UserData other, Contact contact, Manifold oldManifold) {
    }
    
    @Override
    public void postSolve(UserData owner, UserData other, Contact contact, ContactImpulse impulse) {
        Player player = PLAYER_COMP_MAPPER.get(owner.getEntity()).player;
        if (other.isEntity()) {
            Entity ent = other.getEntity();
            if (ITEM_STACK_COMP_MAPPER.has(ent)) {
                ItemStackComponent iscomp = ITEM_STACK_COMP_MAPPER.get(ent);
                if (iscomp.stack != null) {
                    iscomp.stack = InvUtil.insert(player.getInventory(), iscomp.stack);
                    if (iscomp.stack == null) {
                        //TODO despawn item entity
                    }
                }
            }
        }
    }
    
}
