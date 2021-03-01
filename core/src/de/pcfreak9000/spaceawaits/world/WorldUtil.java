package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.physics.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class WorldUtil {
    
    public static void createWorldBorders(Global global, int width, int height) {
        float wf = width * Tile.TILE_SIZE;
        float hf = height * Tile.TILE_SIZE;
        global.addEntity(createEntity(-50, -50, 50, hf + 100));
        global.addEntity(createEntity(0, -50, wf, 50));
        global.addEntity(createEntity(0, hf, wf, 50));
        global.addEntity(createEntity(wf, -50, 50, hf + 100));
    }
    
    private static Entity createEntity(float x, float y, float w, float h) {
        Entity e = new Entity();
        PhysicsComponent ph = new PhysicsComponent();
        ph.factory = AABBBodyFactory.create(w, h, x, y);
        e.add(ph);
        return e;
    }
    
}
