package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.Inventory;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player {
    public static Player ofNBT(NBTCompound readPlayerNBT) {
        return new Player();
    }
    
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
    
}
