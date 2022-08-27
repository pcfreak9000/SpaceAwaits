package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.gui.ContainerInventoryPlayer;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player implements NBTSerializable {
    
    private final Entity playerEntity;
    
    private GameRenderer gameRenderer;
    
    private InventoryPlayer inventory;
    
    public Player(GameRenderer rend) {
        this.gameRenderer = rend;
        this.playerEntity = PlayerEntityFactory.setupPlayerEntity(this);
        this.inventory = new InventoryPlayer();
        //        this.inventory.setSlotContent(0, new ItemStack(GameRegistry.ITEM_REGISTRY.get("grass"), 128));
        //        this.inventory.setSlotContent(1, new ItemStack(GameRegistry.ITEM_REGISTRY.get("stone"), 128));
        //        this.inventory.setSlotContent(2, new ItemStack(GameRegistry.ITEM_REGISTRY.get("laser"), 32));
        //        this.inventory.setSlotContent(3, new ItemStack(GameRegistry.ITEM_REGISTRY.get("dirt"), 128));
        //        this.inventory.setSlotContent(4, new ItemStack(GameRegistry.ITEM_REGISTRY.get("gun"), 1));
        //        this.inventory.setSlotContent(5, new ItemStack(GameRegistry.ITEM_REGISTRY.get("torch"), 128));
        //        this.inventory.setSlotContent(6, new ItemStack(GameRegistry.ITEM_REGISTRY.get("ore_iron"), 64));
        //this.inventory.setSlotContent(7, new ItemStack(GameRegistry.ITEM_REGISTRY.get("bottom"), 10));
    }
    
    public Entity getPlayerEntity() {
        return this.playerEntity;
    }
    
    public InventoryPlayer getInventory() {
        return this.inventory;
    }
    
    //have reach component???
    public boolean isInReach(float x, float y) {
        Vector2 pos = Components.TRANSFORM.get(getPlayerEntity()).position;
        float xdif = x - pos.x;
        float ydif = y - pos.y;
        return Mathf.square(xdif) + Mathf.square(ydif) < Mathf.square(10);
    }
    
    public void openContainer(GuiOverlay container) {
        container.create(this.gameRenderer, this);
        this.gameRenderer.setGuiCurrent(container);
    }
    
    public void openInventory() {
        this.openContainer(new ContainerInventoryPlayer());
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
