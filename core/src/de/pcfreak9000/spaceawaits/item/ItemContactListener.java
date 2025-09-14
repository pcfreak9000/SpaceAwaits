package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.structs.b2Manifold;

import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;

public class ItemContactListener implements IContactListener {

	private SystemCache<EntityInteractSystem> eisys = new SystemCache<>(EntityInteractSystem.class);

	@Override
	public boolean beginContact(UserDataHelper owner, UserDataHelper other, b2Manifold manifold,UnitConversion conv, Engine world) {
		if (other.isEntity()) {
			Entity e = other.getEntity();
			if (Components.ITEM_STACK.has(e)) {
				ItemStackComponent me = Components.ITEM_STACK.get(owner.getEntity());
				ItemStackComponent you = Components.ITEM_STACK.get(e);
				me.stack = ItemStack.join(me.stack, you.stack);
				if (ItemStack.isEmptyOrNull(you.stack)) {
					eisys.get(world).despawnEntity(e);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean endContact(UserDataHelper owner, UserDataHelper other, UnitConversion conv, Engine world) {
		return false;
	}

}
