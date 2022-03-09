package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

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
    private static final float BACKGROUND_FACTOR = 0.55f;
    
    private SpriteCache regionCache;//Maybe use something global instead
    private Camera camera;
    
    private int count;
    
    public RenderTileDefaultStrategy(GameRenderer renderer) {
        super(Family.all(ChunkRenderComponent.class).get());
        this.regionCache = new SpriteCache(5000000, false);//Somewhere get information on how many regions will be cached at once so we can find out the required cache size
        this.camera = renderer.getCurrentView().getCamera();
    }
    
    @Override
    public void begin() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
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
        int len = recacheTiles(regionCache, crc.chunk, crc.layer);
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
    
    private int recacheTiles(SpriteCache cache, Chunk c, TileLayer layer) {
        Color backgroundColor = new Color();
        int len = 0;
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int gtx = i + c.getGlobalTileX();
                int gty = j + c.getGlobalTileY();
                Tile tile = c.getTile(gtx, gty, layer);
                if (!isVisible(tile)) {
                    continue;
                }
                backgroundColor.set(tile.color());
                if (layer == TileLayer.Back) {
                    backgroundColor.mul(BACKGROUND_FACTOR, BACKGROUND_FACTOR, BACKGROUND_FACTOR, 1);
                }
                cache.setColor(backgroundColor);
                addTile(tile, gtx, gty, cache, c, layer);
                len++;
            }
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