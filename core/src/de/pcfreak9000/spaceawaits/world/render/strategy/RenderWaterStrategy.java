package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.cyphercove.flexbatch.FlexBatch;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.ShaderProvider;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.render.SpriteBatchImpr;
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
    
    private SpriteBatchImpr batchSimple;
    
    private FlexBatch<LiquidQuad2D> batch;
    
    private TileSystem tiles;
    
    private FrameBuffer refl;
    
    public RenderWaterStrategy(GameRenderer rend, World world) {
        super(Family.all(ChunkRenderComponent.class).get());
        this.rend = rend;
        this.camera = this.rend.getCurrentView().getCamera();
        this.batch = new FlexBatch<>(LiquidQuad2D.class, 32767, 0);
        this.batchSimple = rend.getSpriteBatch();
        this.batch.setShader(shader.getShader());
        world.getWorldBus().register(this);
        resize();
        //System.out.println(shader.getShader().getLog());
    }
    
    @EventSubscription
    public void event2(RendererEvents.ResizeWorldRendererEvent ev) {
        resize();
    }
    
    private void resize() {
        if (refl != null) {
            this.refl.dispose();
        }
        this.refl = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
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
        this.batchSimple.setDefaultBlending();
        this.batchSimple.setColor(Color.WHITE);
        this.refl.begin();
        ScreenUtils.clear(0, 0, 0, 0);
        this.batchSimple.begin();
        this.rend.getFBOStack().drawAll(batchSimple, camera);
        this.batchSimple.end();
        this.refl.end();
        this.rend.getFBOStack().rebind();
        
        time += Gdx.graphics.getDeltaTime();
        shader.getShader().bind();
        shader.getShader().setUniformf("time", time * 1.8f);
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
        LongArray pos = crc.tilePositions;
        for (int i = 0; i < pos.size; i++) {
            long l = pos.items[i];
            int gtx = IntCoords.xOfLong(l);
            int gty = IntCoords.yOfLong(l);
            TileLiquid tile = (TileLiquid) crc.chunk.getTile(gtx, gty, crc.layer);
            LiquidState s = (LiquidState) crc.chunk.getMetadata(gtx, gty, crc.layer);
            float height = s.getLiquid() / tile.getMaxValue();
            Tile tileAbove = tiles.getTile(gtx, gty + 1, crc.layer);
            boolean inBetween = false;
            float topHeight = height;
            if (tileAbove == tile) {
                LiquidState ns = (LiquidState) tiles.getMetadata(gtx, gty + 1, crc.layer);
                if (!ns.isEmpty()) {
                    topHeight = ns.getLiquid() / tile.getMaxValue();
                    topHeight = MathUtils.clamp(topHeight, 0, 1);
                    if (topHeight == 1f) {
                        //make sure that for interesting parameters the animation doesn't mine too deep and greedy... 
                        topHeight = Float.POSITIVE_INFINITY;
                    }
                    inBetween = true;
                }
            }
            height = MathUtils.clamp(height, 0, 1);
            //the animation has to continue in this cell for low amounts of liquid in the top cell
            //the base is always one tile below liquid level, topLayer is the y-position of the liquid level
            float topLayer = inBetween ? (topHeight + gty + 1) : (height + gty);
            float base = topLayer - 1; //inBetween ? (gty + topHeight) : gty - (1 - height);
            if ((height < 1f && inBetween) || (tileAbove.isSolid() && height > 0.99f)) {
                //liquid is flowing or is quite full and has some solid above it, so just render a full cell without waves
                topLayer = Float.POSITIVE_INFINITY;
                height = 1;
            }
            batch.draw().time(time * 1.8f).distortionStrength(1 / 250f).levelDistortionModifier(20f).lvlThickness(0.4f)
                    .shore(topLayer).base(base).color(tile.color()).texture(refl.getColorBufferTexture())
                    .region(0, 0, 1, 1).position(gtx, gty).size(1, height);
        }
    }
    
    @Override
    public void dispose() {
        this.batch.dispose();
        this.refl.dispose();
    }
}
