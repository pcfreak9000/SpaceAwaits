package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.gui.ContainerInventoryPlayer;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.item.ItemStack;
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
    
    private Array<ItemStack> toDrop = new Array<>(false, 10);
    
    public Player(GameRenderer rend) {
        this.gameRenderer = rend;
        this.playerEntity = PlayerEntityFactory.setupPlayerEntity(this);
        this.inventory = new InventoryPlayer();
    }
    
    public Entity getPlayerEntity() {
        return this.playerEntity;
    }
    
    public InventoryPlayer getInventory() {
        return this.inventory;
    }
    
    public void dropWhenPossible(ItemStack stack) {
        if (!ItemStack.isEmptyOrNull(stack)) {
            this.toDrop.add(stack);
        }
    }
    
    public Array<ItemStack> getDroppingQueue() {
        return this.toDrop;
    }
    
    public float getReach() {
        return 10;
    }
    
    //have reach component??? maybe move this into hand component or so? and then as parameter have an entity?
    public boolean isInReachFromHand(float x, float y, float range) {
        Vector2 pos = Components.TRANSFORM.get(getPlayerEntity()).position;//Hmm. Entity stuff here? oof
        float xdif = x - pos.x;
        float ydif = y - pos.y + 1;
        return Mathf.square(xdif) + Mathf.square(ydif) < Mathf.square(range);
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
