package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import de.omnikryptec.math.MathUtil;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.save.WorldMeta;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldCombined;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator.GeneratorCapabilitiesBase;

public class Game {
    
    //switch worlds etc
    
    private ISave mySave;
    
    private Player player;
    private World world;
    
    public Game(ISave save) {
        this.mySave = save;
        this.player = new Player();
        this.readPlayer();
    }
    
    private void readPlayer() {
        if (this.mySave.hasPlayer()) {
            this.player.readNBT(this.mySave.readPlayerNBT());
        }
    }
    
    public void joinGame() {
        String w = this.player.getCurrentWorld();
        if (this.mySave.hasWorld(w)) {
            this.joinWorld(w);
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
            World world = new WorldCombined(worldPrimer, save);
            this.player.setCurrentWorld(uuid);
            world.joinWorld(player);
            this.world = world;
            SpaceAwaits.BUS.post(new WorldEvents.SetWorldEvent());
            SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().setWorld(world);
        } catch (IOException e) {
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
        SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().setWorld(null);
        this.world.unloadAll();
        mySave.writePlayerNBT((NBTCompound) this.player.writeNBT());
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
}
