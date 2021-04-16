package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.utils.IntSet;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class RenderChunkStrategy extends AbstractRenderStrategy implements EntityListener {
    
    private ComponentMapper<ChunkComponent> tMapper = ComponentMapper.getFor(ChunkComponent.class);
    private ComponentMapper<ChunkRenderComponent> rMapper = ComponentMapper.getFor(ChunkRenderComponent.class);
    private static final float BACKGROUND_FACTOR = 0.5f;
    
    private SpriteCache regionCache;//Maybe use something global instead
    private IntSet freeCacheIds;
    private Camera camera;
    
    public RenderChunkStrategy() {
        super(Family.all(ChunkComponent.class).get());
        this.freeCacheIds = new IntSet();
        this.regionCache = new SpriteCache(5000000, false);//Somewhere get information on how many regions will be cached at once so we can find out the required cache size
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void settwevent(WorldEvents.SetWorldEvent ev) {
        camera = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getCamera();
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
        float[] empty = new float[5 * 6 * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * 2];//5 floats per vertex, 6 vertices per image, REGION_TILE_SIZE^2 images per layer, 2 layers 
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
    public void begin() {
        //this.regionCache.clear();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        regionCache.setProjectionMatrix(
                SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getCamera().combined);
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
        ChunkComponent c = tMapper.get(entity);
        float mx = (c.chunk.getGlobalChunkX() + 0.5f) * Chunk.CHUNK_SIZE;
        float my = (c.chunk.getGlobalChunkY() + 0.5f) * Chunk.CHUNK_SIZE;
        if (!camera.frustum.boundsInFrustum(mx, my, 0, 0.5f * Chunk.CHUNK_SIZE, 0.5f * Chunk.CHUNK_SIZE, 0)) {
            return;
        }
        SpriteCache ca = this.regionCache;
        ca.clear();
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
        Color backgroundColor = new Color();
        crc.len = 0;
        crc.blen = 0;
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int gtx = i + c.getGlobalTileX();
                int gty = j + c.getGlobalTileY();
                Tile tile = c.getBackground(gtx, gty);
                if (!isVisible(tile)) {
                    continue;
                }
                backgroundColor.set(tile.color());
                backgroundColor.mul(BACKGROUND_FACTOR, BACKGROUND_FACTOR, BACKGROUND_FACTOR, 1);
                cache.setColor(backgroundColor);
                addTile(tile, gtx, gty, cache);
                crc.blen++;
                crc.len++;
            }
        }
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int gtx = i + c.getGlobalTileX();
                int gty = j + c.getGlobalTileY();
                Tile tile = c.getTile(gtx, gty);
                if (!isVisible(tile)) {
                    continue;
                }
                cache.setColor(tile.color());
                addTile(c.getTile(gtx, gty), gtx, gty, cache);
                crc.len++;
            }
        }
    }
    
    private boolean isVisible(Tile t) {
        return t.color().a > 0;
    }
    
    private void addTile(Tile t, int gtx, int gty, SpriteCache c) {
        c.add(t.getTextureProvider().getRegion(), gtx, gty, 1, 1);
    }
}