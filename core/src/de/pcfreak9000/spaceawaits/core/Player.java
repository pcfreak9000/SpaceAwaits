package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.item.Inventory;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player implements NBTSerializable {
    
    private final Entity playerEntity;
    
    private Inventory inventory;
    
    private String currentWorld;//Probably temporary until Space comes
    
    public Player() {
        this.playerEntity = CoreResources.PLAYER_FACTORY.createEntity();
        this.inventory = new Inventory();
    }
    
    public Entity getPlayerEntity() {
        return this.playerEntity;
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
    
    public void setCurrentWorld(String currentWorld) {
        this.currentWorld = currentWorld;
    }
    
    public String getCurrentWorld() {
        return currentWorld;
    }
    
    @Override
    public void readNBT(NBTTag compound) {
        NBTCompound pc = (NBTCompound) compound;
        EntitySerializer.deserializeEntityComponents(playerEntity, pc.getCompound("entity"));
        this.currentWorld = pc.getString("currentWorld");
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound pc = new NBTCompound();
        pc.put("entity", EntitySerializer.serializeEntityComponents(playerEntity));
        pc.putString("currentWorld", currentWorld);
        return pc;
    }
    
}
