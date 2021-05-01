package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.item.InventoryPlayer;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player implements NBTSerializable {
    
    private final Entity playerEntity;
    
    private InventoryPlayer inventory;
    
    public Player() {
        this.playerEntity = CoreResources.PLAYER_FACTORY.createEntity();
        this.playerEntity.getComponent(PlayerInputComponent.class).player = this;
        this.inventory = new InventoryPlayer();
        this.inventory.setSlotContent(0, new ItemStack(GameRegistry.ITEM_REGISTRY.get("grass"), 128));
        this.inventory.setSlotContent(1, new ItemStack(GameRegistry.ITEM_REGISTRY.get("stone"), 128));
        this.inventory.setSlotContent(2, new ItemStack(GameRegistry.ITEM_REGISTRY.get("laser"), 32));
        this.inventory.setSlotContent(3, new ItemStack(GameRegistry.ITEM_REGISTRY.get("dirt"), 128));
        this.inventory.setSlotContent(4, new ItemStack(GameRegistry.ITEM_REGISTRY.get("gun"), 1));
        this.inventory.setSlotContent(5, new ItemStack(GameRegistry.ITEM_REGISTRY.get("torch"), 128));
        this.inventory.setSlotContent(6, new ItemStack(GameRegistry.ITEM_REGISTRY.get("ore_iron"), 64));
        //this.inventory.setSlotContent(7, new ItemStack(GameRegistry.ITEM_REGISTRY.get("bottom"), 10));
    }
    
    public Entity getPlayerEntity() {
        return this.playerEntity;
    }
    
    public InventoryPlayer getInventory() {
        return this.inventory;
    }
    
    @Override
    public void readNBT(NBTTag compound) {
        NBTCompound pc = (NBTCompound) compound;
        EntitySerializer.deserializeEntityComponents(playerEntity, pc.getCompound("entity"));
        this.inventory.readNBT(pc.get("inventory"));
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound pc = new NBTCompound();
        pc.put("entity", EntitySerializer.serializeEntityComponents(playerEntity));
        pc.put("inventory", this.inventory.writeNBT());
        return pc;
    }
    
}
