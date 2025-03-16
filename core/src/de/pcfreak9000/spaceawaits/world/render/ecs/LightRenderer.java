package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.ecs.WorldSystem;
import de.pcfreak9000.spaceawaits.world.light.PixelPointLightTask2;
import de.pcfreak9000.spaceawaits.world.render.EffectRenderer;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

//Optimize for the case if everything is lit anyways? the scenebuffer wouldnt be required then
public class LightRenderer implements Disposable, EffectRenderer {
    private static final int extraLightRadius = 25;

    private static final float BEGIN_LIGHT_LAYER = RenderLayers.BEGIN_LIGHT;
    private static final float END_LIGHT_LAYER = RenderLayers.END_LIGHT;

    private Engine world;
    private GameScreen renderer;

    private FrameBuffer lightsBuffer;
    private FrameBuffer sceneBuffer;
    private Texture texture;
    private int gwi, ghi;

    private SystemCache<CameraSystem> camsys = new SystemCache<>(CameraSystem.class);
    private SystemCache<WorldSystem> wsys = new SystemCache<>(WorldSystem.class);

    public LightRenderer(GameScreen renderer) {
        this.renderer = renderer;
        resize();
    }

    @EventSubscription
    public void event2(RendererEvents.ResizeWorldRendererEvent ev) {
        resize();
    }

    public void addedToEngineInternal(Engine engine) {
        ((EngineImproved) engine).getEventBus().register(this);
        this.world = engine;
    }

    public void removedFromEngineInternal(Engine engine) {
        ((EngineImproved) engine).getEventBus().unregister(this);
        this.world = null;
    }

    public void enterLitScene() {
        this.renderer.getRenderHelper().getFBOStack().push(sceneBuffer);
        // sceneBuffer.begin();
        ScreenUtils.clear(0, 0, 0, 0);
    }

    public void resize() {
        if (lightsBuffer != null) {
            this.lightsBuffer.dispose();
        }
        this.lightsBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);// Hmmmm,
                                                                                                                     // width
                                                                                                                     // and
                                                                                                                     // height,
                                                                                                                     // ...?!
        if (sceneBuffer != null) {
            this.sceneBuffer.dispose();
        }
        this.sceneBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    public void exitAndRenderLitScene() {
        this.renderer.getRenderHelper().getFBOStack().pop(sceneBuffer);
        Camera cam = camsys.get(world).getCamera();
        WorldSystem ws = wsys.get(world);
        SpriteBatchImpr batch = renderer.getRenderHelper().getSpriteBatch();
        batch.resetSettings();

        int xi = Tile.toGlobalTile(cam.position.x - cam.viewportWidth / 2) - extraLightRadius;
        int yi = Tile.toGlobalTile(cam.position.y - cam.viewportHeight / 2) - extraLightRadius;
        int wi = Mathf.ceili(cam.viewportWidth);
        int hi = Mathf.ceili(cam.viewportHeight);
        try {
            new PixelPointLightTask2(ws.getBounds(), (pix) -> {
                if (texture != null) {
                    texture.dispose();
                }
                texture = new Texture(pix);
                texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                gwi = texture.getWidth();
                ghi = texture.getHeight();
                pix.dispose();
            }, xi, yi, wi + 2 * extraLightRadius, hi + 2 * extraLightRadius, world.getSystem(TileSystem.class),
                    ws.getAmbientLightProvider()).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }

        this.renderer.getRenderHelper().getFBOStack().push(lightsBuffer);// This framebuffer is good because places
                                                                         // where the light is not yet calculated will
                                                                         // be pitch black
        {
            ScreenUtils.clear(0, 0, 0, 0);
            if (texture != null) {
                batch.setAdditiveBlending();
                batch.begin();
                batch.draw(texture, xi, yi, gwi, ghi);
                batch.end();
            }
        }
        this.renderer.getRenderHelper().getFBOStack().pop(lightsBuffer);
        this.renderer.getRenderHelper().getFBOStack().push(sceneBuffer);
        renderer.getRenderHelper().applyViewport();
        batch.setMultiplicativeBlending();
        batch.begin();
        batch.draw(this.lightsBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.lightsBuffer.getWidth(), this.lightsBuffer.getHeight(), false, true);
        batch.end();
        this.renderer.getRenderHelper().getFBOStack().pop(sceneBuffer);
        batch.setDefaultBlending();
        batch.begin();
        batch.draw(this.sceneBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.sceneBuffer.getWidth(), this.sceneBuffer.getHeight(), false, true);
        batch.end();
    }

    @Override
    public void dispose() {
        Logger.getLogger(getClass()).debug("Disposing...");
        if (this.texture != null) {
            this.texture.dispose();
            this.texture = null;
        }
        if (lightsBuffer != null) {
            this.lightsBuffer.dispose();
        }
    }

    @Override
    public void enterEffect() {
        enterLitScene();
    }

    @Override
    public void exitAndRenderEffect() {
        exitAndRenderLitScene();
    }

    @Override
    public float getBeginLayer() {
        return BEGIN_LIGHT_LAYER;
    }

    @Override
    public float getEndLayer() {
        return END_LIGHT_LAYER;
    }
}
