package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserData;

public class ItemContactListener implements IContactListener {
    private static final ComponentMapper<ItemStackComponent> ITEM_STACK_COMP_MAPPER = ComponentMapper
            .getFor(ItemStackComponent.class);
    
    @Override
    public void beginContact(UserData owner, UserData other, Contact contact, UnitConversion conv, World world) {
    }
    
    @Override
    public void endContact(UserData owner, UserData other, Contact contact, UnitConversion conv, World world) {
    }
    
    @Override
    public void preSolve(UserData owner, UserData other, Contact contact, Manifold oldManifold, UnitConversion conv,
            World world) {
        if (other.isEntity()) {
            Entity e = other.getEntity();
            if (ITEM_STACK_COMP_MAPPER.has(e)) {
                ItemStackComponent me = ITEM_STACK_COMP_MAPPER.get(owner.getEntity());
                ItemStackComponent you = ITEM_STACK_COMP_MAPPER.get(e);
                me.stack = ItemStack.join(me.stack, you.stack);
                if (ItemStack.isEmptyOrNull(you.stack)) {
                    world.despawnEntity(e);
                }
            }
        }
    }
    
    @Override
    public void postSolve(UserData owner, UserData other, Contact contact, ContactImpulse impulse, UnitConversion conv,
            World world) {
    }
    
}
