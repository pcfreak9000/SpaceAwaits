package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.physics.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class WorldUtil {
    
    public static void createWorldBorders(Array<Entity> entities, int width, int height) {
        float wf = width;
        float hf = height;
        entities.add(createBorderEntity(-50, -50, 50, hf + 100));
        entities.add(createBorderEntity(0, -50, wf, 50));
        entities.add(createBorderEntity(0, hf, wf, 50));
        entities.add(createBorderEntity(wf, -50, 50, hf + 100));
    }
    
    private static Entity createBorderEntity(float x, float y, float w, float h) {
        Entity e = new EntityImproved();
        PhysicsComponent ph = new PhysicsComponent();
        ph.factory = AABBBodyFactory.builder().staticBody().dimensions(w, h).initialPosition(x, y).create();//AABBBodyFactory.create(w, h, x, y);
        e.add(ph);
        return e;
    }
    
    public static Vector2 findSpawnpoint(World world, float entWidth, float entHeight, float spawnX, float spawnY,
            float spawnWidth, float spawnHeight) {
        //System.out.println("Finding Spawnpoint");
        RandomXS128 rand = new RandomXS128(world.getSeed());//Hmm
        ChunkProvider chunkProvider = (ChunkProvider) world.chunkProvider; //<-- some other stuff might need easier access as well
        TileSystem ts = world.getSystem(TileSystem.class);
        Object lock = new Object();
        for (int i = 0; i < 100; i++) {
            float x = spawnX + rand.nextFloat() * spawnWidth;
            float y = spawnY + rand.nextFloat() * spawnHeight;
            if (world.getBounds().inBoundsf(x, y)) {
                int cx = Chunk.toGlobalChunkf(x);
                int cy = Chunk.toGlobalChunkf(y);
                int cw = Chunk.toGlobalChunkf(x + entWidth);
                int ch = Chunk.toGlobalChunkf(y + entHeight);
                for (int j = cx; j <= cw; j++) {
                    for (int k = cy; k <= ch; k++) {
                        chunkProvider.requireChunk(j, k, true, lock);
                    }
                }
                if (!ts.checkSolidOccupation(x, y, entWidth, entHeight)) {
                    if (world.getWorldProperties().autoLowerSpawnpointToSolidGround()) {
                        while (true) {
                            y--;
                            cy = Chunk.toGlobalChunkf(y);
                            ch = Chunk.toGlobalChunkf(y + entHeight);
                            for (int j = cx; j <= cw; j++) {
                                for (int k = cy; k <= ch; k++) {
                                    chunkProvider.requireChunk(j, k, true, lock);
                                }
                            }
                            if (ts.checkSolidOccupation(x, y, entWidth, entHeight)) {// || y < spawnArea.y -> strictly enforcing the spawnArea might lead to fall damage and a death loop 
                                y++;
                                break;
                            }
                        }
                    }
                    //can spawn here, so do that
                    chunkProvider.releaseLock(lock);
                    Logger.getLogger(World.class)
                            .debug("Found a spawning location for the player on the " + (i + 1) + ". try");
                    return new Vector2(x, y);
                }
                if (chunkProvider.getLoadedChunkCountLock(lock) > 13) {
                    chunkProvider.releaseLock(lock);
                }
            }
        }
        chunkProvider.releaseLock(lock);
        Logger.getLogger(World.class).debug("Couldn't find a suitable spawning location.");
        //TODO no spawn was found so just pick a random location and forcefully blow a hole into the ground or something
        return null;
    }
}
