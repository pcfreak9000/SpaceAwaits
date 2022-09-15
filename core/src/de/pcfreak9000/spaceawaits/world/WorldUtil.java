package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.content.WorldGlobalComponent;
import de.pcfreak9000.spaceawaits.world.physics.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class WorldUtil {
    
    public static void createWorldBorders(World world, int width, int height) {
        float wf = width;
        float hf = height;
        world.spawnEntity(createBorderEntity(-50, -50, 50, hf + 100), false);
        world.spawnEntity(createBorderEntity(0, -50, wf, 50), false);
        world.spawnEntity(createBorderEntity(0, hf, wf, 50), false);
        world.spawnEntity(createBorderEntity(wf, -50, 50, hf + 100), false);
    }
    
    private static Entity createBorderEntity(float x, float y, float w, float h) {
        Entity e = new EntityImproved();
        PhysicsComponent ph = new PhysicsComponent();
        e.add(new WorldGlobalComponent());
        ph.factory = AABBBodyFactory.builder().staticBody().dimensions(w, h).initialPosition(x, y).create();//AABBBodyFactory.create(w, h, x, y);
        e.add(ph);
        return e;
    }
    
    public static void simImpact(ITileArea tiles, float x, float y, float radius, float velx, float vely,
            float density) {
        float mass = radius * radius * radius * 4 / 3.0f * Mathf.PI * density;
        float velAbs = (float) Math.sqrt(Mathf.square(velx) + Mathf.square(vely));
        int ix = Tile.toGlobalTile(x);
        int iy = Tile.toGlobalTile(y);
        int irad = Mathf.ceili(radius);
        for (int i = -irad; i < irad; i++) {
            for (int j = -irad; j < irad; j++) {
                if (Mathf.square(i) + Mathf.square(j) < Mathf.square(irad)) {
                    int tex = ix + i;
                    int tey = iy + j;
                    tiles.setTile(tex, tey, TileLayer.Front, Tile.NOTHING);//Hmmmm.
                }
            }
        }
    }
    
    //TODO refine spawning system
    public static Vector2 findSpawnpoint(World world, float entWidth, float entHeight, float spawnX, float spawnY,
            float spawnWidth, float spawnHeight) {
        RandomXS128 rand = new RandomXS128(world.getSeed());//Hmm
        PhysicsSystem ps = world.getSystem(PhysicsSystem.class);
        for (int i = 0; i < 100; i++) {
            float x = spawnX + rand.nextFloat() * spawnWidth;
            float y = spawnY + rand.nextFloat() * spawnHeight;
            if (world.getBounds().inBoundsf(x, y)) {
                if (!ps.checkRectOccupation(x, y, entWidth, entHeight, false)) {
                    if (world.getWorldProperties().autoLowerSpawnpointToSolidGround()) {
                        while (true) {
                            y--;
                            if (ps.checkRectOccupation(x, y, entWidth, entHeight, false)) {// || y < spawnArea.y -> strictly enforcing the spawnArea might lead to fall damage and a death loop 
                                y++;
                                break;
                            }
                        }
                    }
                    //can spawn here, so do that
                    return new Vector2(x, y);
                }
            }
        }
        //no spawn was found so just pick a random location and forcefully blow a hole into the ground or something?
        return null;
    }
}
