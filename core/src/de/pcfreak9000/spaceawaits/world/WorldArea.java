package de.pcfreak9000.spaceawaits.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.EntityInteractSystem.SpawnState;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class WorldArea implements ITileArea {
    
    private IChunkProvider chunkProvider;
    private Bounds bounds;
    private World world;
    
    private List<Entity> entitiesToSpawn = new ArrayList<>();
    
    public WorldArea(IChunkProvider prov, Bounds bounds, World active) {
        this.chunkProvider = prov;
        this.bounds = bounds;
        this.world = active;
    }
    
    private Chunk getChunkForTile(int tx, int ty) {
        if (inBounds(tx, ty)) {
            Chunk c = chunkProvider.getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
            return c;
        }
        return null;
    }
    
    public SpawnState spawnEntity(Entity entity, boolean checkOccupation) {
        if (Components.TRANSFORM.has(entity) && Components.PHYSICS.has(entity) && checkOccupation) {
            entitiesToSpawn.add(entity);
            return SpawnState.Pending;
        }
        if (Components.TRANSFORM.has(entity) && !Components.GLOBAL_MARKER.has(entity)) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = this.chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            if (c == null || (c.isActive() && world == null)) {
                return SpawnState.Failure;//Not so nice, this way the entity is just forgotten 
            }
            c.addEntity(entity);
            if (c.isActive()) {
                world.ecsEngine.addEntity(entity);
            }
            return SpawnState.Success;
        }
        entitiesToSpawn.add(entity);
        return SpawnState.Pending;
    }
    
    public List<Entity> getEntitiesToSpawn() {
        return entitiesToSpawn;
    }
    
    @Override
    public boolean inBounds(int tx, int ty) {
        return bounds.inBounds(tx, ty);
    }
    
    @Override
    public ITileEntity getTileEntity(int tx, int ty, TileLayer layer) {
        return getChunkForTile(tx, ty).getTileEntity(tx, ty, layer);
    }
    
    @Override
    public Tile getTile(int tx, int ty, TileLayer layer) {
        return getChunkForTile(tx, ty).getTile(tx, ty, layer);
    }
    
    @Override
    public Tile setTile(int tx, int ty, TileLayer layer, Tile t) {
        return getChunkForTile(tx, ty).setTile(tx, ty, layer, t);
    }
    
    @Override
    public Tile removeTile(int tx, int ty, TileLayer layer) {
        return setTile(tx, ty, layer, this.world.getWorldProperties().getTileDefault(tx, ty, layer));
    }
    
}
