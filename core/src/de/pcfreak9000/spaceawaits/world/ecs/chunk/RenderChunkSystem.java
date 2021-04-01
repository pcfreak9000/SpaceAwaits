package de.pcfreak9000.spaceawaits.world.ecs.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.utils.IntSet;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileState;

public class RenderChunkSystem extends IteratingSystem implements EntityListener {
    
    private ComponentMapper<ChunkComponent> tMapper = ComponentMapper.getFor(ChunkComponent.class);
    private ComponentMapper<ChunkRenderComponent> rMapper = ComponentMapper.getFor(ChunkRenderComponent.class);
    private static final float BACKGROUND_FACTOR = 0.5f;
    
    private SpriteCache regionCache;//Maybe use something global instead
    private IntSet freeCacheIds;
    private Camera camera;
    
    public RenderChunkSystem() {
        super(Family.all(ChunkComponent.class).get());
        this.freeCacheIds = new IntSet();
        this.regionCache = new SpriteCache(5000000, false);//Somewhere get information on how many regions will be cached at once so we can find out the required cache size
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void settwevent(WorldEvents.SetWorldEvent ev) {
        camera = SpaceAwaits.getSpaceAwaits().getScreenStateManager().getWorldRenderer().getCamera();
    }
    
    @EventSubscription
    private void event2(CoreEvents.ExitEvent ex) {
        this.regionCache.dispose();
    }
    
    @Override
    public void entityAdded(Entity entity) {
        ChunkRenderComponent crc = rMapper.get(entity);
        int cacheId;
        if (freeCacheIds.isEmpty()) {
            cacheId = createCache();
        } else {
            cacheId = freeCacheIds.first();
            freeCacheIds.remove(cacheId);
        }
        crc.cid = cacheId;
        crc.dirty = true;
    }
    
    private int createCache() {
        regionCache.beginCache();
        float[] empty = new float[5 * 6 * Chunk.CHUNK_TILE_SIZE * Chunk.CHUNK_TILE_SIZE * 2];//5 floats per vertex, 6 vertices per image, REGION_TILE_SIZE^2 images per layer, 2 layers 
        regionCache.add(null, empty, 0, empty.length);
        return regionCache.endCache();
        //Dont allocate too many caches -> use some pooling or something (only regions that are loaded need a cache)
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        ChunkRenderComponent crc = rMapper.get(entity);
        freeCacheIds.add(crc.cid);
        crc.cid = -1;
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
    public void update(float deltaTime) {
        //this.regionCache.clear();
        regionCache.setProjectionMatrix(SpaceAwaits.getSpaceAwaits().getScreenStateManager().getWorldRenderer().getCamera().combined);
        for (int i = 0; i < getEntities().size(); i++) {
            processEntity(getEntities().get(i), deltaTime);
        }
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ChunkComponent c = tMapper.get(entity);
        float mx = (c.chunk.getGlobalChunkX() + 0.5f) * Chunk.CHUNK_TILE_SIZE * Tile.TILE_SIZE;
        float my = (c.chunk.getGlobalChunkY() + 0.5f) * Chunk.CHUNK_TILE_SIZE * Tile.TILE_SIZE;
        if (!camera.frustum.boundsInFrustum(mx, my, 0, 0.5f * Chunk.CHUNK_TILE_SIZE * Tile.TILE_SIZE,
                0.5f * Chunk.CHUNK_TILE_SIZE * Tile.TILE_SIZE, 0)) {
            return;
        }
        SpriteCache ca = this.regionCache;
        this.regionCache.clear();
        ca.beginCache();
        ChunkRenderComponent crc = rMapper.get(entity);
        recacheTiles(regionCache, c.chunk, crc);
        int id = ca.endCache();
        ca.begin();
        ca.draw(id, 0, crc.len);
        ca.end();
        //        
        //                if (c.region.recacheTiles()) {
        //                    regionCache.beginCache(c.region.cacheId);
        //                    c.region.recacheTiles(regionCache);
        //                    regionCache.endCache();
        //                }
        //                regionCache.setProjectionMatrix(
        //                        SpaceAwaits.getSpaceAwaits().getWorldManager().getRenderInfo().getCamera().combined);
        //                regionCache.begin();
        //                if (c.region.len() > 0) {
        //                    regionCache.draw(c.region.cacheId, 0, c.region.len());
        //                }
        //                regionCache.end();
        //        SpriteBatch b = SpaceAwaits.getSpaceAwaits().getWorldManager().getRenderInfo().getSpriteBatch();
        //        b.begin();
        //        c.region.recacheTiles(b);
        //        b.end();
    }
    
    private void recacheTiles(SpriteCache cache, Chunk c, ChunkRenderComponent crc) {
        List<TileState> tiles = new ArrayList<>();
        Predicate<TileState> predicate = (t) -> t.getTile().color().a > 0;//Maybe just iterate the whole tilestorage or something, first collecting everything is probably slow
        //background does not need to be recached all the time because it can not change (rn)?
        c.tileBackgroundAll(tiles, predicate);
        Color backgroundColor = new Color();
        crc.len = 0;
        crc.blen = 0;
        for (TileState t : tiles) {
            backgroundColor.set(t.getTile().color());
            backgroundColor.mul(BACKGROUND_FACTOR, BACKGROUND_FACTOR, BACKGROUND_FACTOR, 1);
            cache.setColor(backgroundColor);
            addTile(t, cache);
            crc.blen++;
            crc.len++;
        }
        tiles.clear();
        c.tileAll(tiles, predicate);
        for (TileState t : tiles) {
            cache.setColor(t.getTile().color());
            addTile(t, cache);
            crc.len++;
        }
    }
    
    private void addTile(TileState t, SpriteCache c) {
        c.add(t.getTile().getTextureProvider().getRegion(), t.getGlobalTileX() * Tile.TILE_SIZE,
                t.getGlobalTileY() * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);
    }
}