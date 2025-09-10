package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.flat.FlatScreen;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.Components;

public class FlatWorldPlayer implements INBTSerializable {

	private final Entity playerEntity;

	private Player player;

	public FlatWorldPlayer(Player player) {
		this.player = player;
		this.playerEntity = null;
	}
	
	public void leaveFlatWorld(FlatScreen fs) {
		fs.getEngine().removeEntity(getPlayerEntity());
		// this.locationUuid = null; //<- this is currently buggy and would lead to a
		// new spawnpoint each time the world is joined...
		fs.getEngine().getEventBus().post(new WorldEvents.PlayerLeftEvent(player));
	}

	public void joinFlatWorld(FlatScreen fs) {
		Components.TRANSFORM.get(playerEntity).position.set(0, 0);
		fs.getEngine().addEntity(getPlayerEntity());
		fs.getEngine().getEventBus().post(new WorldEvents.PlayerJoinedEvent(player));
	}

	public Entity getPlayerEntity() {
		return this.playerEntity;
	}

	@Override
	public void readNBT(NBTCompound nbt) {
	}

	@Override
	public void writeNBT(NBTCompound nbt) {
	}

}
