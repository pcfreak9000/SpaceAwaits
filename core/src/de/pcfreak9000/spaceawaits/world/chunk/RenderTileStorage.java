package de.pcfreak9000.spaceawaits.world.chunk;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.ObjectMap;

import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderMarkerComp;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class RenderTileStorage {
    
    private ObjectMap<Object, LongArray> storage;
    private ObjectMap<Object, Entity> entities;
    private final float renderlayer;
    private final TileLayer tilelayer;
    private final Chunk chunk;
    
    public RenderTileStorage(float renderlayer, Chunk chunk, TileLayer tilelayer) {
        this.storage = new ObjectMap<>();
        this.entities = new ObjectMap<>();
        this.renderlayer = renderlayer;
        this.chunk = chunk;
        this.tilelayer = tilelayer;
    }
    
    public void addTilePos(RenderMarkerComp rendererId, int tx, int ty) {
        long l = IntCoords.toLong(tx, ty);
        LongArray array = storage.get(rendererId);
        if (array == null) {
            array = new LongArray(false, 16);
            storage.put(rendererId, array);
            Entity e = new EntityImproved();
            e.add(new RenderComponent(renderlayer + rendererId.layeroffset));//Uhoh...
            e.add(rendererId);
            ChunkRenderComponent crc = new ChunkRenderComponent();
            crc.chunk = this.chunk;
            crc.layer = this.tilelayer;
            crc.tilePositions = array;
            e.add(crc);
            this.entities.put(rendererId, e);
            this.chunk.addEntity(e);
            Engine ecs = this.chunk.getECS();
            if (ecs != null) {
                ecs.addEntity(e);
            }
        }
        array.add(l);
    }
    
    public void removeTilePos(RenderMarkerComp rendererId, int tx, int ty) {
        long l = IntCoords.toLong(tx, ty);
        LongArray array = storage.get(rendererId);
        if (array != null) {
            array.removeValue(l);
            if (array.isEmpty()) {
                storage.remove(rendererId);
                Entity e = this.entities.remove(rendererId);
                e.flags = 4206969;
                this.chunk.removeEntity(e);
                Engine ecs = this.chunk.getECS();
                if (ecs != null) {
                    ecs.removeEntity(e);
                }
                //e.removeAll(); //oh no, delayed entity removal causes some trouble if this is used... 
            }
        }
    }
    
    //RenderStrategies should use ChunkRenderComponent for access
    public LongArray getTilesPosFor(RenderMarkerComp rendererId) {
        return storage.get(rendererId);
    }
    
}
