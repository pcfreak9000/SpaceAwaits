package de.pcfreak9000.spaceawaits.core;

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

public class Game {
    
    //switch worlds etc
    
    private ISave mySave;
    
    private Player player;
    
    private WorldManager worldMgr = SpaceAwaits.getSpaceAwaits().getWorldManager(); //This will change when WorldManager becomes more modular
    
    public Game(ISave save, Player player) {
        this.mySave = save;
        this.player = player;
        this.worldMgr.getWorldAccess().addLoadingBounds(
                new WorldLoadingBounds(this.player.getPlayerEntity().getComponent(TransformComponent.class).position));//Oh boi
    }
    
    public void joinWorld(String uuid) {
        IWorldSave save = this.mySave.getWorld(uuid);
        WorldMeta meta = save.getWorldMeta();
        String genId = meta.getWorldGeneratorUsed();
        long worldSeed = meta.getWorldSeed();
        WorldGenerator gen = GameRegistry.GENERATOR_REGISTRY.get(genId); //TODO default WorldGenerator
        WorldGenerationBundle bundle = gen.generateWorld(worldSeed);
        SaveWorldProvider provider = new SaveWorldProvider(bundle.getChunkGenerator(), bundle.getGlobalGenerator(),
                save, new WorldBounds(meta.getWidth(), meta.getHeight(), meta.isWrapsAround()));//Hmmm - do this a better way
        worldMgr.getWorldAccess().setWorldProvider(provider);
        //Add player todo
    }
    
    public String createAndJoinWorld(WorldGenerator generator, String name, long seed) {
        WorldGenerationBundle worldGenBundle = generator.generateWorld(seed);
        WorldMeta wMeta = new WorldMeta();
        wMeta.setDisplayName(name);
        wMeta.setWorldSeed(seed);
        wMeta.setLastPlayed(System.currentTimeMillis());
        wMeta.setWorldGeneratorUsed(GameRegistry.GENERATOR_REGISTRY.getId(generator));
        wMeta.setWidth(worldGenBundle.getBounds().getWidth());
        wMeta.setHeight(worldGenBundle.getBounds().getHeight());
        wMeta.setWrapsAround(worldGenBundle.getBounds().isWrappingAround());
        String uuid = this.mySave.createWorld(name, wMeta);
        IWorldSave worldSave = this.mySave.getWorld(uuid);
        SaveWorldProvider provider = new SaveWorldProvider(worldGenBundle.getChunkGenerator(),
                worldGenBundle.getGlobalGenerator(), worldSave, worldGenBundle.getBounds());
        worldMgr.getECSManager().addEntity(player.getPlayerEntity());//Oof, find a better place to add the player
        worldMgr.getWorldAccess().setWorldProvider(provider);
        return uuid;
    }
    
    public void saveAndLeaveCurrentWorld() {
        worldMgr.getWorldAccess().setWorldProvider(null);
        mySave.writePlayerNBT((NBTCompound) this.player.writeNBT());
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
}
