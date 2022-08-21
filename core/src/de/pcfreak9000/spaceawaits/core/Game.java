package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.MathUtil;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.save.WorldMeta;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldCombined;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.gen.WorldSetup;
import de.pcfreak9000.spaceawaits.world.gen.WorldSetup.GeneratorCapabilitiesBase;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class Game {
    
    private static final Logger LOGGER = Logger.getLogger(Game.class);
    
    //switch worlds etc
    
    private ISave mySave;
    private GameRenderer gameRenderer;
    
    private Player player;
    private String uuidPlayerLocation;
    private World world;
    
    private boolean fresh;//TMP!!! also what if the spawn world is deleted? that shoudl then be replaced by another spawn world
    
    public Game(ISave save, GameRenderer renderer, boolean fresh) {
        this.mySave = save;
        this.gameRenderer = renderer;
        this.player = new Player(renderer);
        this.fresh = fresh;
        this.readPlayer();
    }
    
    //probably also TMP
    public void joinGame() {
        if (this.mySave.hasWorld(uuidPlayerLocation)) {
            this.joinWorld(uuidPlayerLocation);
        } else {
            //Check if this save has a spawn place, otherwise generate a new one
            //When generating a new world, place the player at spawn
            String id = createWorld("Gurke",
                    pickGenerator(GameRegistry.GENERATOR_REGISTRY.filtered(GeneratorCapabilitiesBase.LVL_ENTRY)),
                    this.mySave.getSaveMeta().getSeed());//Derive world seed from that master seed instead of using it directly
            joinWorld(id);
        }
    }
    
    //TMP
    private WorldSetup pickGenerator(List<WorldSetup> list) {
        return MathUtil.getWeightedRandom(new Random(), list);
    }
    
    public void joinWorld(String uuid) {
        try {
            LOGGER.infof("Setting up world for joining...");
            IWorldSave save = this.mySave.getWorld(uuid);
            WorldMeta meta = save.getWorldMeta();
            String genId = meta.getWorldGeneratorUsed();
            long worldSeed = meta.getWorldSeed();
            //No default because this is crucial information and can't really be defaulted
            WorldSetup gen = GameRegistry.GENERATOR_REGISTRY.get(genId);
            WorldPrimer worldPrimer = gen.setupWorld(new GeneratorSettings(worldSeed, fresh));
            fresh = false;
            worldPrimer.setWorldBounds(new WorldBounds(meta.getWidth(), meta.getHeight()));
            WorldCombined world = new WorldCombined(worldPrimer, save, worldSeed, gameRenderer);
            boolean newLocation = !Objects.equals(uuidPlayerLocation, uuid);//The player is not currently on this location so a spawn point needs to be found...
            this.world = world;
            //worldPrimer.getWorldGenerator().generate(world);
            this.uuidPlayerLocation = uuid;
            if (newLocation) {
                LOGGER.info("Looking for a spawnpoint...");
                Vector2 spawnpoint = worldPrimer.getPlayerSpawn().getPlayerSpawn(player, world);
                Vector2 playerpos = Components.TRANSFORM.get(player.getPlayerEntity()).position;
                playerpos.x = spawnpoint.x;
                playerpos.y = spawnpoint.y;
                OnSolidGroundComponent osgc = player.getPlayerEntity().getComponent(OnSolidGroundComponent.class);
                osgc.lastContactX = spawnpoint.x;
                osgc.lastContactY = spawnpoint.y;
            }
            LOGGER.info("Joining world...");
            world.joinWorld(player);
            this.gameRenderer.setWorldView();
            this.gameRenderer.getWorldView().setWorld(world);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String createWorld(String name, WorldSetup generator, long seed) {
        LOGGER.infof("Creating world...");
        WorldPrimer worldPrimer = generator.setupWorld(new GeneratorSettings(seed, fresh));
        fresh = false;
        WorldMeta wMeta = WorldMeta.builder().displayName(name).worldSeed(seed).createdNow()
                .worldGenerator(GameRegistry.GENERATOR_REGISTRY.getId(generator))
                .dimensions(worldPrimer.getWorldBounds()).create();
        //The meta can probably be cached (useful for create-and-join)
        try {
            String uuid = this.mySave.createWorld(name, wMeta);
            return uuid;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void saveAndLeaveCurrentWorld() {
        this.gameRenderer.getWorldView().setWorld(null);
        this.world.unloadAll();
        this.writePlayer();
    }
    
    public void saveAll() {
        WorldCombined w = (WorldCombined) world;
        w.saveAll();
        this.writePlayer();
    }
    
    private void readPlayer() {
        if (this.mySave.hasPlayer()) {
            NBTCompound savestate = mySave.readPlayerNBT();
            this.player.readNBT(savestate.get("player"));
            this.uuidPlayerLocation = savestate.getString("currentLocation");
        }
    }
    
    private void writePlayer() {
        NBTCompound comp = new NBTCompound();
        comp.put("player", this.player.writeNBT());
        comp.putString("currentLocation", uuidPlayerLocation);
        mySave.writePlayerNBT(comp);
    }
    
    public World getWorldCurrent() {
        return this.world;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
}
