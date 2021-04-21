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
    
    private String currentWorld;//Probably temporary until Space comes
    
    public Player() {
        this.playerEntity = CoreResources.PLAYER_FACTORY.createEntity();
        this.playerEntity.getComponent(PlayerInputComponent.class).player = this;
        this.inventory = new InventoryPlayer();
        this.inventory.setSlotContent(0, new ItemStack(GameRegistry.ITEM_REGISTRY.get("grass"), 20));
        this.inventory.setSlotContent(1, new ItemStack(GameRegistry.ITEM_REGISTRY.get("stone"), 35));
        this.inventory.setSlotContent(2, new ItemStack(GameRegistry.ITEM_REGISTRY.get("laser"), 35));
        this.inventory.setSlotContent(3, new ItemStack(GameRegistry.ITEM_REGISTRY.get("dirt"), 35));
        this.inventory.setSlotContent(4, new ItemStack(GameRegistry.ITEM_REGISTRY.get("gun"), 1));
        this.inventory.setSlotContent(5, new ItemStack(GameRegistry.ITEM_REGISTRY.get("torch"), 10));
        this.inventory.setSlotContent(6, new ItemStack(GameRegistry.ITEM_REGISTRY.get("ore_iron"), 10));
        this.inventory.setSlotContent(7, new ItemStack(GameRegistry.ITEM_REGISTRY.get("bottom"), 10));
    }
    
    public Entity getPlayerEntity() {
        return this.playerEntity;
    }
    
    public InventoryPlayer getInventory() {
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
