package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongArray;

import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.render.GameScreen;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class RenderTileDefaultStrategy extends AbstractRenderStrategy implements Disposable {
    
    private SpriteCache regionCache;//Maybe use something global instead
    private GameScreen gameScreen;
    private Camera camera;
    
    private int count;
    
    public RenderTileDefaultStrategy(GameScreen renderer) {
        super(Family.all(ChunkRenderComponent.class, RenderTileDefaultMarkerComponent.class).get());
        this.gameScreen = renderer;
        this.regionCache = new SpriteCache(5000000, false);//Somewhere get information on how many regions will be cached at once so we can find out the required cache size
        this.camera = renderer.getCamera();
    }
    
    @Override
    public void begin() {
        this.gameScreen.setDefaultBlending();
        regionCache.setProjectionMatrix(camera.combined);
        this.count = 0;
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
        ChunkRenderComponent crc = Components.RENDER_CHUNK.get(entity);
        if (!Util.checkChunkInFrustum(crc.chunk, camera)) {
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
            backgroundColor.set(tile.getColor());
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
        return t.getColor().a > 0;
    }
    
    private void addTile(Tile t, int gtx, int gty, SpriteCache c, Chunk chunk, TileLayer layer) {
        //TODOx provide a tilearea that is readonly of the tilesystem instead of chunk, possibly even confined to a small space around the tile
        c.add(t.getTexture().getRegion(), gtx, gty, 1, 1);
    }
    
    public int getRenderedChunkCount() {
        return count;
    }
}