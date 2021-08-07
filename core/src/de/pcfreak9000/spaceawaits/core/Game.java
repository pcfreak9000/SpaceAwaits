package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.MathUtil;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.save.WorldMeta;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldCombined;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator.GeneratorCapabilitiesBase;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class Game {
    
    //switch worlds etc
    
    private ISave mySave;
    private GameRenderer gameRenderer;
    
    private Player player;
    private String uuidPlayerLocation;
    private World world;
    
    public Game(ISave save, GameRenderer renderer) {
        this.mySave = save;
        this.gameRenderer = renderer;
        this.player = new Player(renderer);
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
    private WorldGenerator pickGenerator(List<WorldGenerator> list) {
        return MathUtil.getWeightedRandom(new Random(), list);
    }
    
    public void joinWorld(String uuid) {
        try {
            IWorldSave save = this.mySave.getWorld(uuid);
            WorldMeta meta = save.getWorldMeta();
            String genId = meta.getWorldGeneratorUsed();
            long worldSeed = meta.getWorldSeed();
            //No default because this is crucial information and can't really be defaulted
            WorldGenerator gen = GameRegistry.GENERATOR_REGISTRY.get(genId);
            WorldPrimer worldPrimer = gen.generateWorld(worldSeed);
            worldPrimer.setWorldBounds(new WorldBounds(meta.getWidth(), meta.getHeight()));
            WorldCombined world = new WorldCombined(worldPrimer, save, worldSeed, gameRenderer);
            boolean newLocation = !Objects.equals(uuidPlayerLocation, uuid);//The player is not currently on this location so a spawn point needs to be found...
            this.uuidPlayerLocation = uuid;
            this.world = world;
            if (newLocation) {
                Vector2 spawnpoint = world.findSpawnpoint(player);
                Vector2 playerpos = CoreRes.TRANSFORM_M.get(player.getPlayerEntity()).position;
                playerpos.x = spawnpoint.x;
                playerpos.y = spawnpoint.y;
                OnSolidGroundComponent osgc = player.getPlayerEntity().getComponent(OnSolidGroundComponent.class);
                osgc.lastContactX = spawnpoint.x;
                osgc.lastContactY = spawnpoint.y;
            }
            world.joinWorld(player);
            this.gameRenderer.setWorldView().setWorld(world);
        } catch (
        
        IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String createWorld(String name, WorldGenerator generator, long seed) {
        WorldPrimer worldPrimer = generator.generateWorld(seed);
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
        this.gameRenderer.setWorldView().setWorld(null);
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
