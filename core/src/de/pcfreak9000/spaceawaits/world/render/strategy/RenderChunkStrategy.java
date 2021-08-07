package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntSet;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

//TODO having there global (gameregistrey and stuff) might not be the best idea
public class RenderChunkStrategy extends AbstractRenderStrategy implements EntityListener, Disposable {
    
    private ComponentMapper<ChunkComponent> tMapper = ComponentMapper.getFor(ChunkComponent.class);
    private ComponentMapper<ChunkRenderComponent> rMapper = ComponentMapper.getFor(ChunkRenderComponent.class);
    private static final float BACKGROUND_FACTOR = 0.5f;
    
    private SpriteCache regionCache;//Maybe use something global instead
    private IntSet freeCacheIds;
    private Camera camera;
    
    private int count;
    
    public RenderChunkStrategy(GameRenderer renderer) {
        super(Family.all(ChunkComponent.class).get());
        this.freeCacheIds = new IntSet();
        this.regionCache = new SpriteCache(5000000, false);//Somewhere get information on how many regions will be cached at once so we can find out the required cache size
        this.camera = renderer.getView().getCamera();
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
                SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().getView().getCamera().combined);
        this.count = 0;
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
        this.count++;
    }
    
    @Override
    public void dispose() {
        this.regionCache.dispose();
    }
    
    private void recacheTiles(SpriteCache cache, Chunk c, ChunkRenderComponent crc) {
        Color backgroundColor = new Color();
        crc.len = 0;
        crc.blen = 0;
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int gtx = i + c.getGlobalTileX();
                int gty = j + c.getGlobalTileY();
                Tile tile = c.getTile(gtx, gty, TileLayer.Back);
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
                Tile tile = c.getTile(gtx, gty, TileLayer.Front);
                if (!isVisible(tile)) {
                    continue;
                }
                cache.setColor(tile.color());
                addTile(tile, gtx, gty, cache);
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
    
    public int getRenderedChunkCount() {
        return count;
    }
}