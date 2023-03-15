package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.MathUtil;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.save.WorldMeta;
import de.pcfreak9000.spaceawaits.screen.ScreenManager;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldCombined;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;

public class Game {
    
    private static final Logger LOGGER = Logger.getLogger(Game.class);
    
    //switch worlds etc
    
    private ISave mySave;
    private ScreenManager scm;//Meh
    
    private Player player;
    private String uuidPlayerLocation;
    private World world;
    
    private boolean fresh;//TMP!!! also what if the spawn world is deleted? that shoudl then be replaced by another spawn world
    
    public Game(ISave save, boolean fresh, ScreenManager scm) {
        this.mySave = save;
        this.fresh = fresh;
        this.scm = scm;
    }
    
    public long getMasterSeed() {
        return mySave.getSaveMeta().getSeed();
    }
    
    public void loadGame() {
        this.player = new Player();
        this.readPlayer();
    }
    
    public void unloadGame() {
        saveAndLeaveCurrentWorld();
        writePlayer();
    }
    
    public void saveGame() {
        WorldCombined w = (WorldCombined) world;
        w.saveWorld();
        writePlayer();
    }
    
    //probably also TMP
    public void joinGame() {
        if (this.mySave.hasWorld(uuidPlayerLocation)) {
            this.joinWorld(uuidPlayerLocation);
        } else {
            //Check if this save has a spawn place, otherwise generate a new one
            //When generating a new world, place the player at spawn
            String id = createWorld("A nice World", pickGenerator(Registry.GENERATOR_REGISTRY.getGens()),
                    this.mySave.getSaveMeta().getSeed());//TODO Derive world seed from that master seed instead of using it directly
            joinWorld(id);
        }
    }
    
    //TMP!!!!
    private IGeneratingLayer<WorldPrimer, GeneratorSettings> pickGenerator(
            Set<IGeneratingLayer<WorldPrimer, GeneratorSettings>> set) {
        List<IGeneratingLayer<WorldPrimer, GeneratorSettings>> list = new ArrayList<>();
        list.addAll(set);
        return MathUtil.getRandom(new Random(), list);
    }
    
    public void saveAndLeaveCurrentWorld() {
        this.world.removePlayer(player);
        this.world.unloadWorld();
        this.world = null;
    }
    
    public void joinWorld(String uuid) {
        try {
            LOGGER.infof("Setting up world for joining...");
            IWorldSave save = this.mySave.getWorld(uuid);
            WorldMeta meta = save.getWorldMeta();
            String genId = meta.getWorldGeneratorUsed();
            long worldSeed = meta.getWorldSeed();
            //No default because this is crucial information and can't really be defaulted
            IGeneratingLayer<WorldPrimer, GeneratorSettings> gen = (IGeneratingLayer<WorldPrimer, GeneratorSettings>) Registry.GENERATOR_REGISTRY
                    .get(genId);
            WorldPrimer worldPrimer = gen.generate(new GeneratorSettings(worldSeed, fresh));
            fresh = false;
            worldPrimer.setWorldBounds(new WorldBounds(meta.getWidth(), meta.getHeight()));
            WorldCombined world = new WorldCombined(worldPrimer, save);
            boolean newLocation = !Objects.equals(uuidPlayerLocation, uuid);//The player is not currently on this location so a spawn point needs to be found...
            this.world = world;
            this.uuidPlayerLocation = uuid;
            LOGGER.info("Joining world...");
            scm.setWorldScreen(world, player);//Replace scm with something that onle allows non-null GameScreens?
            if (newLocation) {//hmmmmmmm
                LOGGER.info("Looking for a spawnpoint...");
                Vector2 spawnpoint = worldPrimer.getPlayerSpawn().getPlayerSpawn(player, world);
                Vector2 playerpos = Components.TRANSFORM.get(player.getPlayerEntity()).position;
                playerpos.x = spawnpoint.x;
                playerpos.y = spawnpoint.y;
                OnSolidGroundComponent osgc = player.getPlayerEntity().getComponent(OnSolidGroundComponent.class);
                osgc.lastContactX = spawnpoint.x;
                osgc.lastContactY = spawnpoint.y;
            }
            world.setPlayer(player);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String createWorld(String name, IGeneratingLayer<WorldPrimer, GeneratorSettings> generator, long seed) {
        LOGGER.infof("Creating world...");
        WorldPrimer worldPrimer = generator.generate(new GeneratorSettings(seed, fresh));
        fresh = false;
        WorldMeta wMeta = WorldMeta.builder().displayName(name).worldSeed(seed).createdNow()
                .worldGenerator(Registry.GENERATOR_REGISTRY.getId(generator)).dimensions(worldPrimer.getWorldBounds())
                .create();
        //The meta can probably be cached (useful for create-and-join)
        try {
            String uuid = this.mySave.createWorld(name, wMeta);
            return uuid;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public World getWorldCurrent() {
        return this.world;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    private void readPlayer() {
        if (this.mySave.hasPlayer()) {
            NBTCompound savestate = mySave.readPlayerNBT();
            this.player.readNBT(savestate.getCompound("player"));
            this.uuidPlayerLocation = savestate.getString("currentLocation");
        }
    }
    
    private void writePlayer() {
        NBTCompound comp = new NBTCompound();
        comp.put("player", INBTSerializable.writeNBT(player));
        comp.putString("currentLocation", uuidPlayerLocation);
        mySave.writePlayerNBT(comp);
    }
    
}
