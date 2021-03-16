package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.Inventory;
import de.pcfreak9000.spaceawaits.save.NBTSerializable;

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
    
    public NBTCompound toNBTCompound() {
        return new NBTCompound();
    }
    
    @Override
    public void readNBT(NBTCompound compound) {
    }
    
    @Override
    public NBTCompound writeNBT() {
        return null;
    }
    
}
