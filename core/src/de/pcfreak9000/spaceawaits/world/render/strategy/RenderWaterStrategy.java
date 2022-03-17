package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongArray;
import com.cyphercove.flexbatch.FlexBatch;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.ShaderProvider;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.water.LiquidQuad2D;
import de.pcfreak9000.spaceawaits.world.tile.LiquidState;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileLiquid;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class RenderWaterStrategy extends AbstractRenderStrategy implements Disposable {
    
    private ComponentMapper<ChunkRenderComponent> rMapper = ComponentMapper.getFor(ChunkRenderComponent.class);
    
    private GameRenderer rend;
    private Camera camera;
    
    private ShaderProvider shader = CoreRes.WATER_SHADER;
    
    //private SpriteBatchImpr batch;
    
    private FlexBatch<LiquidQuad2D> batch;
    
    private TileSystem tiles;
    
    public RenderWaterStrategy(GameRenderer rend) {
        super(Family.all(ChunkRenderComponent.class).get());
        this.rend = rend;
        this.camera = this.rend.getCurrentView().getCamera();
        this.batch = new FlexBatch<>(LiquidQuad2D.class, 32767, 0);
        //this.batch = new SpriteBatchImpr(8191);
        this.batch.setShader(shader.getShader());
        System.out.println(shader.getShader().getLog());
    }
    
    @Override
    protected void addedToEngine(Engine engine) {
        this.tiles = engine.getSystem(TileSystem.class);
    }
    
    @Override
    protected void removedFromEngine(Engine engine) {
        this.tiles = null;
    }
    
    float time = 0;
    
    @Override
    public void begin() {
        time += Gdx.graphics.getDeltaTime();
        shader.getShader().bind();
        shader.getShader().setUniformf("time", time*3f);
        shader.getShader().setUniformf("size", 1f, 1f);
        batch.setProjectionMatrix(this.camera.combined);
        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
        // batch.setDefaultBlending();
        batch.begin();
    }
    
    @Override
    public void end() {
        batch.end();
    }
    
    @Override
    public void render(Entity e, float dt) {
        ChunkRenderComponent crc = rMapper.get(e);
        float mx = (crc.chunk.getGlobalChunkX() + 0.5f) * Chunk.CHUNK_SIZE;
        float my = (crc.chunk.getGlobalChunkY() + 0.5f) * Chunk.CHUNK_SIZE;
        if (!camera.frustum.boundsInFrustum(mx, my, 0, 0.5f * Chunk.CHUNK_SIZE, 0.5f * Chunk.CHUNK_SIZE, 0)) {
            return;
        }
        //FlexBatch<Quad2D> b = new FlexBatch<>(Quad2D.class, 32767, 0);
        LongArray pos = crc.tilePositions;
        //        Color backgroundColor = new Color();
        for (int i = 0; i < pos.size; i++) {
            long l = pos.items[i];
            int gtx = IntCoords.xOfLong(l);
            int gty = IntCoords.yOfLong(l);
            TileLiquid tile = (TileLiquid) crc.chunk.getTile(gtx, gty, crc.layer);
            if (!isVisible(tile)) {
                continue;
            }
            //            backgroundColor.set(tile.color());
            //            if (crc.layer == TileLayer.Back) {
            //                backgroundColor.mul(Tile.BACKGROUND_FACTOR, Tile.BACKGROUND_FACTOR, Tile.BACKGROUND_FACTOR, 1);
            //            }
            //batch.setColor(tile.color());
            LiquidState s = (LiquidState) crc.chunk.getMetadata(gtx, gty, crc.layer);
            float height = s.getLiquid() / tile.getMaxValue();
            Tile neigh = tiles.getTile(gtx, gty + 1, crc.layer);
            if (neigh == tile) {
                LiquidState ns = (LiquidState) tiles.getMetadata(gtx, gty + 1, crc.layer);
                if (!ns.isEmpty()) {
                    height = 1;
                }
            }
            height = MathUtils.clamp(height, 0, 1);
            batch.draw().color(tile.color()).textureRegion(tile.getTextureProvider().getRegion()).position(gtx, gty)
                    .size(1, height);
            //batch.draw(tile.getTextureProvider().getRegion(), gtx, gty, 1, height);
        }
    }
    
    private boolean isVisible(Tile t) {
        return t.color().a > 0;
    }
    
    @Override
    public void dispose() {
        this.batch.dispose();
    }
}
