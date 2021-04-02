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
import de.pcfreak9000.spaceawaits.world.SaveWorldProvider;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldLoadingBounds;
import de.pcfreak9000.spaceawaits.world.WorldManager;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerationBundle;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator.GeneratorCapabilitiesBase;

public class Game {
    
    //switch worlds etc
    
    private ISave mySave;
    
    private Player player;
    
    private WorldManager worldMgr = SpaceAwaits.getSpaceAwaits().getWorldManager(); //This will change when WorldManager becomes more modular
    
    public Game(ISave save) {
        this.mySave = save;
        this.player = new Player();
        this.readPlayer();
    }
    
    private void readPlayer() {
        if (this.mySave.hasPlayer()) {
            this.player.readNBT(this.mySave.readPlayerNBT());
        }
        this.worldMgr.getWorldAccess().addLoadingBounds(
                new WorldLoadingBounds(this.player.getPlayerEntity().getComponent(TransformComponent.class).position));//Oh boi
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
            WorldGenerator gen = GameRegistry.GENERATOR_REGISTRY.get(genId); //TODO default WorldGenerator
            WorldGenerationBundle bundle = gen.generateWorld(worldSeed);
            SaveWorldProvider provider = new SaveWorldProvider(bundle.getChunkGenerator(), bundle.getGlobalGenerator(),
                    save, new WorldBounds(meta.getWidth(), meta.getHeight(), meta.isWrapsAround()));//Hmmm - do this a better way
            worldMgr.getECSManager().addEntity(player.getPlayerEntity());//Oof, find a better place to add the player
            this.player.setCurrentWorld(uuid);
            worldMgr.getWorldAccess().setWorldProvider(provider);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String createWorld(String name, WorldGenerator generator, long seed) {
        WorldGenerationBundle worldGenBundle = generator.generateWorld(seed);
        WorldMeta wMeta = new WorldMeta();
        wMeta.setDisplayName(name);
        wMeta.setWorldSeed(seed);
        wMeta.setCreated(System.currentTimeMillis());
        wMeta.setWorldGeneratorUsed(GameRegistry.GENERATOR_REGISTRY.getId(generator));
        wMeta.setWidth(worldGenBundle.getBounds().getWidth());
        wMeta.setHeight(worldGenBundle.getBounds().getHeight());
        wMeta.setWrapsAround(worldGenBundle.getBounds().isWrappingAround());
        //The meta can probably be cached (useful for create-and-join)
        try {
            String uuid = this.mySave.createWorld(name, wMeta);
            return uuid;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void saveAndLeaveCurrentWorld() {
        worldMgr.getECSManager().removeEntity(player.getPlayerEntity());
        worldMgr.getWorldAccess().setWorldProvider(null);
        mySave.writePlayerNBT((NBTCompound) this.player.writeNBT());
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
}
