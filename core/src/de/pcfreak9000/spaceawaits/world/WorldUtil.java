package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.world.physics.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;

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
        Entity e = new Entity();
        PhysicsComponent ph = new PhysicsComponent();
        ph.factory = AABBBodyFactory.builder().staticBody().dimensions(w, h).initialPosition(x, y).create();//AABBBodyFactory.create(w, h, x, y);
        e.add(ph);
        return e;
    }
    
}
