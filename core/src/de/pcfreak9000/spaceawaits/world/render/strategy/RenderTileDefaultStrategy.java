package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongArray;

import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.tile.LiquidState;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.TileLiquid;

//TODO having there global (gameregistrey and stuff) might not be the best idea
public class RenderTileDefaultStrategy extends AbstractRenderStrategy implements Disposable {
    
    private ComponentMapper<ChunkRenderComponent> rMapper = ComponentMapper.getFor(ChunkRenderComponent.class);
    
    private SpriteCache regionCache;//Maybe use something global instead
    private GameRenderer gameRenderer;
    private Camera camera;
    
    private int count;
    
    public RenderTileDefaultStrategy(GameRenderer renderer) {
        super(Family.all(ChunkRenderComponent.class).get());
        this.gameRenderer = renderer;
        this.regionCache = new SpriteCache(5000000, false);//Somewhere get information on how many regions will be cached at once so we can find out the required cache size
        this.camera = renderer.getCurrentView().getCamera();
    }
    
    @Override
    public void begin() {
        this.gameRenderer.setDefaultBlending();
        regionCache.setProjectionMatrix(camera.combined);
        this.count = 0;
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
        ChunkRenderComponent crc = rMapper.get(entity);
        float mx = (crc.chunk.getGlobalChunkX() + 0.5f) * Chunk.CHUNK_SIZE;
        float my = (crc.chunk.getGlobalChunkY() + 0.5f) * Chunk.CHUNK_SIZE;
        if (!camera.frustum.boundsInFrustum(mx, my, 0, 0.5f * Chunk.CHUNK_SIZE, 0.5f * Chunk.CHUNK_SIZE, 0)) {
            return;
        }
        SpriteCache ca = this.regionCache;
        ca.clear();
        ca.beginCache();
        int len = recacheTiles(regionCache, crc.chunk, crc.layer, crc.tilePositions);
        int id = ca.endCache();
        ca.begin();
        ca.draw(id, 0, len);
        ca.end();
        this.count++;
    }
    
    @Override
    public void dispose() {
        this.regionCache.dispose();
    }
    
    private int recacheTiles(SpriteCache cache, Chunk c, TileLayer layer, LongArray poss) {
        Color backgroundColor = new Color();
        int len = 0;
        for (int i = 0; i < poss.size; i++) {
            long l = poss.items[i];
            int gtx = IntCoords.xOfLong(l);
            int gty = IntCoords.yOfLong(l);
            Tile tile = c.getTile(gtx, gty, layer);
            if (!isVisible(tile)) {
                continue;
            }
            backgroundColor.set(tile.color());
            if (layer == TileLayer.Back) {
                backgroundColor.mul(Tile.BACKGROUND_FACTOR, Tile.BACKGROUND_FACTOR, Tile.BACKGROUND_FACTOR, 1);
            }
            cache.setColor(backgroundColor);
            addTile(tile, gtx, gty, cache, c, layer);
            len++;
            
        }
        return len;
    }
    
    private boolean isVisible(Tile t) {
        return t.color().a > 0;
    }
    
    private void addTile(Tile t, int gtx, int gty, SpriteCache c, Chunk chunk, TileLayer layer) {
        float height = 1;
        if (t instanceof TileLiquid) {
            TileLiquid tl = (TileLiquid) t;
            LiquidState s = (LiquidState) chunk.getMetadata(gtx, gty, layer);
            height = s.getLiquid() / tl.getMaxValue();
            height = MathUtils.clamp(height, 0, 1);
        }
        c.add(t.getTextureProvider().getRegion(), gtx, gty, 1, height);
    }
    
    public int getRenderedChunkCount() {
        return count;
    }
}