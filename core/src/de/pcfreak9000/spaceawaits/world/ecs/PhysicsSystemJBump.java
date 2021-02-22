package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response.Result;
import com.dongbat.jbump.World;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldEvents.ChunkLoadedEvent;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class PhysicsSystemJBump extends IteratingSystem implements EntityListener {
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    
    private static final float STEPSIZE_SECONDS = 1 / 100f;
    
    private float deltaAcc = 0;
    
    private World<Object> physicsWorld;
    
    public PhysicsSystemJBump() {
        super(Family.all(PhysicsComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
        this.physicsWorld = new World<>();
    }
    
    @EventSubscription
    private void chunkloadedev(ChunkLoadedEvent ev) {
        //TMP
        for (int i = 0; i < Chunk.CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_TILE_SIZE; j++) {
                int x = ev.chunk.getGlobalTileX() + i;
                int y = ev.chunk.getGlobalTileY() + j;
                Tile t = ev.chunk.getTile(x, y);
                if (t.isSolid()) {
                    physicsWorld.add(new Item<>(t), x * Tile.TILE_SIZE, y * Tile.TILE_SIZE, Tile.TILE_SIZE,
                            Tile.TILE_SIZE);
                }
            }
        }
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), this);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }
    
    @Override
    public void update(float deltat) {
        this.deltaAcc += deltat;
        if (deltaAcc > 10 * STEPSIZE_SECONDS) {
            Logger.getLogger(PhysicsSystem.class).warnf("Skipping physics ticks, acc. physics time: %f", deltaAcc);
            deltaAcc = 10 * STEPSIZE_SECONDS;
        }
        while (deltaAcc >= STEPSIZE_SECONDS) {
            deltaAcc -= STEPSIZE_SECONDS;
            for (Entity e : getEntities()) {
                processEntity(e, deltat);
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = this.transformMapper.get(entity);
        PhysicsComponent pc = this.physicsMapper.get(entity);
        float posDeltaX = 0.5f * pc.acceleration.x * Mathf.square(STEPSIZE_SECONDS) + pc.velocity.x * STEPSIZE_SECONDS;
        float posDeltaY = 0.5f * pc.acceleration.y * Mathf.square(STEPSIZE_SECONDS) + pc.velocity.y * STEPSIZE_SECONDS;
        pc.velocity.add(pc.acceleration.x * STEPSIZE_SECONDS, pc.acceleration.y * STEPSIZE_SECONDS);
        Item<Object> item = pc.item;
        float goalX = posDeltaX + tc.position.x;
        float goalY = posDeltaY + tc.position.y;
        Result result = physicsWorld.move(item, goalX, goalY, CollisionFilter.defaultFilter);
//        if (!result.projectedCollisions.isEmpty()) {
//            for (int i = 0; i < result.projectedCollisions.size(); i++) {
//                Collision collision = result.projectedCollisions.get(i);
//                if (collision.other.userData instanceof Tile) {
//                    IntPoint normal = collision.normal;
//                    pc.velocity.sub(new Vector2(normal.x, normal.y).scl(1 * pc.velocity.dot(normal.x, normal.y)));
//                }
//            }
//        }
        Rect rect = physicsWorld.getRect(item);
        tc.position.set(rect.x,rect.y);
        pc.x = tc.position.x;
        pc.y = tc.position.y;
    }
    
    @Override
    public void entityAdded(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        TransformComponent tc = this.transformMapper.get(entity);
        if (pc.w != 0 || pc.h != 0) {
            if (pc.item == null) {
                pc.item = new Item<>();
            }
            pc.x = tc.position.x;
            pc.y = tc.position.y;
            physicsWorld.add(pc.item, pc.x, pc.y, pc.w, pc.h);
        }
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        if (pc.w != 0 || pc.h != 0) {
            physicsWorld.remove(pc.item);
        }
    }
}
