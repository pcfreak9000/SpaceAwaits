package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.core.ContainerTesting;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.screen.TileScreen;
import de.pcfreak9000.spaceawaits.flat.FlatScreen;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.knowledge.Experiences;
import de.pcfreak9000.spaceawaits.knowledge.Knowledgebase;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldSystem;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player implements INBTSerializable {

    public static enum GameMode {
        Survival(false), Testing(true), TestingGhost(true);

        public final boolean isTesting;

        private GameMode(boolean istesting) {
            this.isTesting = istesting;
        }
    }

    private String locationUuid = null;

    private final Entity playerEntity;

    private InventoryPlayer inventory;

    private Knowledgebase knowledges;
    private Experiences experiences;

    private GameMode gameMode = GameMode.Survival;

    // Hmmmmm...
    private Array<ItemStack> toDrop = new Array<>(false, 10);

    public Player() {
        this.playerEntity = PlayerEntityFactory.setupPlayerEntity(this);
        this.inventory = new InventoryPlayer();
        this.knowledges = new Knowledgebase();
        this.experiences = new Experiences();
    }

    public void joinTileWorld(TileScreen ts) {
        if (locationUuid == null) {// hmmmmmmm, check for new location
            // LOGGER.info("Looking for a spawnpoint...");
            Vector2 spawnpoint = ts.getSystem(WorldSystem.class).getPlayerSpawn().getPlayerSpawn(this, ts.getECS());// TODO
                                                                                                                    // engine
            Vector2 playerpos = Components.TRANSFORM.get(getPlayerEntity()).position;
            playerpos.x = spawnpoint.x;
            playerpos.y = spawnpoint.y;
            OnSolidGroundComponent osgc = getPlayerEntity().getComponent(OnSolidGroundComponent.class);
            osgc.lastContactX = spawnpoint.x;
            osgc.lastContactY = spawnpoint.y;
        }
        this.locationUuid = ts.getUUID();
        ts.getECS().addEntity(getPlayerEntity());
        ((EngineImproved) ts.getECS()).getEventBus().post(new WorldEvents.PlayerJoinedEvent(this));
    }

    public void leaveTileWorld(TileScreen ts) {
        ts.getECS().removeEntity(getPlayerEntity());
        // this.locationUuid = null; //<- this is currently buggy and would lead to a
        // new spawnpoint each time the world is joined...
        ((EngineImproved) ts.getECS()).getEventBus().post(new WorldEvents.PlayerLeftEvent(this));
    }

    public void leaveFlatWorld(FlatScreen fs) {
        fs.getEngine().removeEntity(getPlayerEntity());
        // this.locationUuid = null; //<- this is currently buggy and would lead to a
        // new spawnpoint each time the world is joined...
        fs.getEngine().getEventBus().post(new WorldEvents.PlayerLeftEvent(this));
    }

    public void joinFlatWorld(FlatScreen fs) {
        Components.TRANSFORM.get(playerEntity).position.set(0, 0);
        fs.getEngine().addEntity(getPlayerEntity());
        fs.getEngine().getEventBus().post(new WorldEvents.PlayerJoinedEvent(this));
    }

    public String getLocationUuid() {
        return locationUuid;
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

    public InventoryPlayer getInventory() {
        return this.inventory;
    }

    public StatsComponent getStats() {// Move stats?
        return getPlayerEntity().getComponent(StatsComponent.class);
    }

    public Knowledgebase getKnowledge() {
        return this.knowledges;
    }

    public Experiences getExperience() {
        return this.experiences;
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
        return getGameMode().isTesting ? 200 : 10;// -> ReachComponent or something...
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
        if (gameMode.isTesting) {
            this.openContainer(new ContainerTesting());
        } else {
            this.openContainer(new ContainerInventoryPlayer());
        }
    }

    @Override
    public void readNBT(NBTCompound pc) {
        this.locationUuid = pc.getStringOrDefault("currentLocationUuid", "");
        if (this.locationUuid.isEmpty()) {
            this.locationUuid = null;
        }
        EntitySerializer.deserializeEntityComponents(playerEntity, pc.getCompound("entity"));
        this.inventory.readNBT(pc.getCompound("inventory"));
        this.knowledges.readNBT(pc.getCompound("science"));
        this.experiences.readNBT(pc.getCompound("experience"));
        NBTList todropl = pc.getListOrDefault("todrop", new NBTList(NBTType.Compound));
        for (int i = 0; i < todropl.size(); i++) {
            toDrop.add(ItemStack.readNBT(todropl.getCompound(i)));
        }
        this.gameMode = GameMode.values()[(int) pc.getIntegerSmartOrDefault("gamemode", GameMode.Survival.ordinal())];
    }

    @Override
    public void writeNBT(NBTCompound pc) {
        pc.putString("currentLocationUuid", locationUuid == null ? "" : locationUuid);
        pc.put("entity", EntitySerializer.serializeEntityComponents(playerEntity));
        pc.put("inventory", INBTSerializable.writeNBT(inventory));
        pc.put("science", INBTSerializable.writeNBT(knowledges));
        pc.put("experience", INBTSerializable.writeNBT(experiences));
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
