package de.pcfreak9000.spaceawaits.world.ecs;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public class EntityInteractSystem extends IteratingSystem {
    
    public static enum SpawnState {
        Success, Pending, Failure;
    }
    
    //private World world;
    private Random random;
    
    private SystemCache<PhysicsSystem> phys = new SystemCache<>(PhysicsSystem.class);
    
    public EntityInteractSystem(Random random) {
        super(Family.all(BreakingComponent.class).get());
        this.random = random;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BreakingComponent bc = Components.BREAKING.get(entity);
        if (bc.last == bc.progress) {
            entity.remove(BreakingComponent.class);
            return;
        }
        
    }
    
    public float breakEntity(IBreaker breaker, Entity entity) {
        if (!Components.BREAKABLE.has(entity)) {
            return IBreaker.ABORTED_BREAKING;
        }
        Destructible destr = Components.BREAKABLE.get(entity).destructable;
        if (!destr.canBreak()) {
            return IBreaker.ABORTED_BREAKING;
        }
        if (!breaker.canBreak(getEngine(), destr)) {
            return IBreaker.ABORTED_BREAKING;
        }
        BreakingComponent bc = Components.BREAKING.get(entity);
        if (bc == null) {
            bc = new BreakingComponent();//Maybe pool breakingcomponent?
            entity.add(bc);
        }
        float speedActual = breaker.breakIt(getEngine(), destr, bc.progress);
        bc.last = bc.progress;
        bc.progress += speedActual * World.STEPLENGTH_SECONDS;
        if (bc.progress >= IBreaker.FINISHED_BREAKING) {
            entity.remove(BreakingComponent.class);
            BreakableComponent breakableComponent = Components.BREAKABLE.get(entity);
            boolean validated = breakableComponent.validate(entity);
            Array<ItemStack> drops = new Array<>();
            Random worldRandom = random;
            if (validated) {
                breakableComponent.breakable.collectDrops(getEngine(), worldRandom, entity, drops);
                breakableComponent.breakable.onEntityBreak(getEngine(), entity, breaker);
            }
            breaker.onBreak(getEngine(), breakableComponent.destructable, drops, worldRandom);
            this.despawnEntity(entity);
            if (drops.size > 0) {
                TransformComponent tc = Components.TRANSFORM.get(entity);
                ItemStack.dropRandomInTile(drops, getEngine(), tc.position.x, tc.position.y, random);
                drops.clear();
            }
            return IBreaker.FINISHED_BREAKING;
        }
        return MathUtils.clamp(bc.progress, 0, 1);
    }
    
    public SpawnState spawnEntity(Entity entity, boolean checkOccupation) {
        //what happens if the chunk is not loaded? -> the chunk gets loaded if this World has the generating backend, but spawning should only happen there anyways
        //what happens if the coordinates are somewhere out of bounds? the entity isn't spawned and simply forgotten (return false)
        if (Components.TRANSFORM.has(entity) && Components.PHYSICS.has(entity) && checkOccupation) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            PhysicsComponent pc = Components.PHYSICS.get(entity);
            Vector2 wh = pc.factory.boundingBoxWidthAndHeight();
            if (phys.get(getEngine()).checkRectOccupation(t.position.x + wh.x / 4, t.position.y + wh.y / 4, wh.x / 2,
                    wh.y / 2, false)) {
                return SpawnState.Failure;
            }
        }
        if (Components.TRANSFORM.has(entity) && !Components.GLOBAL_MARKER.has(entity)) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = getEngine().getSystem(ChunkSystem.class).getChunk(supposedChunkX, supposedChunkY);
            if (c == null) {
                return SpawnState.Failure;//Not so nice, this way the entity is just forgotten 
            }
            c.addEntityAC(entity);
        } else {
            getEngine().getSystem(WorldSystem.class).addEntity(entity);
        }
        return SpawnState.Success;
    }
    
    public void despawnEntity(Entity entity) {
        if (Components.CHUNK.has(entity)) {
            Chunk c = Components.CHUNK.get(entity).currentChunk;
            if (c != null) {
                c.removeEntityAC(entity);
            }
        } else if (Components.TRANSFORM.has(entity) && !Components.GLOBAL_MARKER.has(entity)) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = getEngine().getSystem(ChunkSystem.class).getChunk(supposedChunkX, supposedChunkY);
            c.removeEntityAC(entity);
        } else {
            getEngine().getSystem(WorldSystem.class).removeEntity(entity);
        }
    }
}
