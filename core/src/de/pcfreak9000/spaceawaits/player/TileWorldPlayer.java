package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.screen.TileScreen;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldSystem;

public class TileWorldPlayer implements INBTSerializable {
	private String locationUuid = null;

	private final Entity playerEntity;

	private Player player;

	public TileWorldPlayer(Player player) {
		this.player = player;
		this.playerEntity = PlayerEntityFactory.setupPlayerEntity(player);
	}

	public void joinTileWorld(TileScreen ts) {
		if (locationUuid == null) {// hmmmmmmm, check for new location
			// LOGGER.info("Looking for a spawnpoint...");
			Vector2 spawnpoint = ts.getSystem(WorldSystem.class).getPlayerSpawn().getPlayerSpawn(player,
					ts.getEngine());
			Vector2 playerpos = Components.TRANSFORM.get(getPlayerEntity()).position;
			playerpos.x = spawnpoint.x;
			playerpos.y = spawnpoint.y;
			OnSolidGroundComponent osgc = getPlayerEntity().getComponent(OnSolidGroundComponent.class);
			osgc.lastContactX = spawnpoint.x;
			osgc.lastContactY = spawnpoint.y;
		}
		this.locationUuid = ts.getUUID();
		ts.getEngine().addEntity(getPlayerEntity());
		player.getKnowledge().register(ts.getWorldBus());
		player.getExperience().register(ts.getWorldBus());
		((EngineImproved) ts.getEngine()).getEventBus().post(new WorldEvents.PlayerJoinedEvent(player));
		// make sure the chunks around the player are loaded before rendering starts.
		// The issue occurs as chunks are loaded in the logic loop meaning the loading
		// might occur less often on fast systems than the rendering, i.e. there are
		// frames with rendering but no update in logic, leading to frames where chunks
		// are not loaded/rendered. this might affect teleporting as well, but i dont
		// care at the moment...
		ts.getEngine().getSystem(ChunkSystem.class).update(0);
	}

	public void leaveTileWorld(TileScreen ts) {
		ts.getEngine().removeEntity(getPlayerEntity());
		// this.locationUuid = null; //<- this is currently buggy and would lead to a
		// new spawnpoint each time the world is joined...
		((EngineImproved) ts.getEngine()).getEventBus().post(new WorldEvents.PlayerLeftEvent(player));
		player.getKnowledge().unregister();
		player.getExperience().unregister();
	}

	public void dropQueue(Engine world) {
		Vector2 pos = Components.TRANSFORM.get(playerEntity).position;
		for (ItemStack s : player.getToDrop()) {
			s.drop(world, pos.x, pos.y);
		}
		player.getToDrop().clear();
	}

	public String getLocationUuid() {
		return locationUuid;
	}

	public Entity getPlayerEntity() {
		return this.playerEntity;
	}

	@Override
	public void readNBT(NBTCompound nbt) {
		this.locationUuid = nbt.getStringOrDefault("currentLocationUuid", "");
		if (this.locationUuid.isEmpty()) {
			this.locationUuid = null;
		}
		EntitySerializer.deserializeEntityComponents(playerEntity, nbt.getCompound("entity"));
	}

	@Override
	public void writeNBT(NBTCompound nbt) {
		nbt.putString("currentLocationUuid", locationUuid == null ? "" : locationUuid);
		nbt.put("entity", EntitySerializer.serializeEntityComponents(playerEntity));
	}
}
