package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.flat.HudSupplier;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.science.Science;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player implements INBTSerializable, HudSupplier {
    
    public static enum GameMode {
        Survival(false), Testing(true), TestingGhost(true);
        
        public final boolean isTesting;
        
        private GameMode(boolean istesting) {
            this.isTesting = istesting;
        }
    }
    
    private final Entity playerEntity;
    
    private InventoryPlayer inventory;
    
    private Science science;
    
    private GameMode gameMode = GameMode.Survival;
    
    private Array<ItemStack> toDrop = new Array<>(false, 10);
    
    public Player() {
        this.playerEntity = PlayerEntityFactory.setupPlayerEntity(this);
        this.inventory = new InventoryPlayer();
        this.science = new Science();
    }
    
    public GameMode getGameMode() {
        return gameMode;
    }
    
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
    }
    
    public Entity getPlayerEntity() {
        return this.playerEntity;
    }
    
    @Override
    public InventoryPlayer getInventory() {
        return this.inventory;
    }
    
    @Override
    public StatsComponent getStats() {
        return getPlayerEntity().getComponent(StatsComponent.class);
    }
    
    public Science getScience() {
        return this.science;
    }
    
    public void dropWhenPossible(ItemStack stack) {
        if (!ItemStack.isEmptyOrNull(stack)) {
            this.toDrop.add(stack);
        }
    }
    
    public void dropQueue(Engine world) {
        Vector2 pos = Components.TRANSFORM.get(playerEntity).position;
        for (ItemStack s : this.toDrop) {
            s.drop(world, pos.x, pos.y);
        }
        this.toDrop.clear();
    }
    
    public float getReach() {
        return getGameMode().isTesting ? 200 : 10;
    }
    
    // have reach component??? maybe move this into hand component or so? and then
    // as parameter have an entity?
    public boolean isInReachFromHand(float x, float y, float range) {
        Vector2 pos = Components.TRANSFORM.get(getPlayerEntity()).position;// Hmm. Entity stuff here? oof
        float xdif = x - pos.x;
        float ydif = y - pos.y + 1;
        return Mathf.square(xdif) + Mathf.square(ydif) < Mathf.square(range);
    }
    
    public void openContainer(GuiOverlay container) {
        container.createAndOpen(this);
    }
    
    public void openInventory() {
        this.openContainer(new ContainerInventoryPlayer());
    }
    
    @Override
    public void readNBT(NBTCompound pc) {
        EntitySerializer.deserializeEntityComponents(playerEntity, pc.getCompound("entity"));
        this.inventory.readNBT(pc.getCompound("inventory"));
        this.science.readNBT(pc.getCompound("science"));
        NBTList todropl = pc.getListOrDefault("todrop", new NBTList(NBTType.Compound));
        for (int i = 0; i < todropl.size(); i++) {
            toDrop.add(ItemStack.readNBT(todropl.getCompound(i)));
        }
        this.gameMode = GameMode.values()[(int) pc.getIntegerSmartOrDefault("gamemode", GameMode.Survival.ordinal())];
    }
    
    @Override
    public void writeNBT(NBTCompound pc) {
        pc.put("entity", EntitySerializer.serializeEntityComponents(playerEntity));
        pc.put("inventory", INBTSerializable.writeNBT(inventory));
        pc.put("science", INBTSerializable.writeNBT(science));
        NBTList todropl = new NBTList(NBTType.Compound);
        for (ItemStack s : toDrop) {
            if (!ItemStack.isEmptyOrNull(s)) {
                todropl.add(ItemStack.writeNBT(s, new NBTCompound()));
            }
        }
        pc.put("todrop", todropl);
        pc.putIntegerSmart("gamemode", gameMode.ordinal());
    }
    
}
