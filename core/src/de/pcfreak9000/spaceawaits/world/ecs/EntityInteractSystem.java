package de.pcfreak9000.spaceawaits.world.ecs;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.IChunkProvider;
import de.pcfreak9000.spaceawaits.world.IUnchunkProvider;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public class EntityInteractSystem extends EntitySystem {
    
    private World world;
    private IChunkProvider chunkProvider;
    private IUnchunkProvider unchunkProvider;
    
    private SystemCache<PhysicsSystem> phys = new SystemCache<>(PhysicsSystem.class);
    
    public EntityInteractSystem(World world, IChunkProvider chunkProvider, IUnchunkProvider unchunkprovider) {
        setProcessing(false);
        this.world = world;
        this.chunkProvider = chunkProvider;
        this.unchunkProvider = unchunkprovider;
    }
    
    public float breakEntity(IBreaker breaker, Entity entity) {
        if (!Components.BREAKABLE.has(entity)) {
            return IBreaker.ABORTED_BREAKING;
        }
        Destructible destr = Components.BREAKABLE.get(entity).destructable;
        if (!destr.canBreak()) {
            return IBreaker.ABORTED_BREAKING;
        }
        if (!breaker.canBreak(world, destr)) {
            return IBreaker.ABORTED_BREAKING;
        }
        BreakingComponent bc = Components.BREAKING.get(entity);
        if (bc == null) {
            bc = new BreakingComponent();//Maybe pool breakingcomponent?
            entity.add(bc);
        }
        float speedActual = breaker.breakIt(world, destr, bc.progress);
        bc.last = bc.progress;
        bc.progress += speedActual * World.STEPLENGTH_SECONDS;
        if (bc.progress >= IBreaker.FINISHED_BREAKING) {
            entity.remove(BreakingComponent.class);
            BreakableComponent breakableComponent = Components.BREAKABLE.get(entity);
            boolean validated = breakableComponent.validate(entity);
            Array<ItemStack> drops = new Array<>();
            Random worldRandom = world.getWorldRandom();
            if (validated) {
                breakableComponent.breakable.collectDrops(world, worldRandom, entity, drops);
                breakableComponent.breakable.onEntityBreak(world, entity, breaker);
            }
            breaker.onBreak(world, breakableComponent.destructable, drops, worldRandom);
            this.despawnEntity(entity);
            if (drops.size > 0) {
                TransformComponent tc = Components.TRANSFORM.get(entity);
                ItemStack.dropRandomInTile(drops, world, tc.position.x, tc.position.y);
                drops.clear();
            }
            return IBreaker.FINISHED_BREAKING;
        }
        return MathUtils.clamp(bc.progress, 0, 1);
    }
    
    public boolean spawnEntity(Entity entity, boolean checkOccupation) {
        //what happens if the chunk is not loaded? -> the chunk gets loaded if this World has the generating backend, but spawning should only happen there anyways
        //what happens if the coordinates are somewhere out of bounds? the entity isn't spawned and simply forgotten (return false)
        if (Components.TRANSFORM.has(entity) && Components.PHYSICS.has(entity) && checkOccupation) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            PhysicsComponent pc = Components.PHYSICS.get(entity);
            Vector2 wh = pc.factory.boundingBoxWidthAndHeight();
            if (phys.get(getEngine()).checkRectOccupation(t.position.x + wh.x / 4, t.position.y + wh.y / 4, wh.x / 2,
                    wh.y / 2, false)) {
                return false;
            }
        }
        if (Components.TRANSFORM.has(entity) && !Components.GLOBAL_MARKER.has(entity)) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = this.chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            if (c == null) {
                return false;//Not so nice, this way the entity is just forgotten 
            }
            c.addEntity(entity);
            if (c.isActive()) {
                getEngine().addEntity(entity);
            }
        } else {
            //Hmmm...
            unchunkProvider.get().addEntity(entity);
            getEngine().addEntity(entity);
        }
        return true;
    }
    
    public void despawnEntity(Entity entity) {
        if (Components.CHUNK.has(entity)) {
            Chunk c = Components.CHUNK.get(entity).currentChunk;
            if (c != null) {
                c.removeEntity(entity);
            }
        } else if (Components.TRANSFORM.has(entity) && !Components.GLOBAL_MARKER.has(entity)) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = this.chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            c.removeEntity(entity);
        }
        unchunkProvider.get().removeEntity(entity);
        getEngine().removeEntity(entity);
    }
}
