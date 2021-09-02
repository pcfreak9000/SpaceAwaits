package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.light.PixelPointLightTask2;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

//Optimize for the case if everything is lit anyways? the scenebuffer wouldnt be required then
public class LightRenderer implements Disposable {
    private static final int extraLightRadius = 25;
    
    private World world;
    private GameRenderer renderer;
    
    private FrameBuffer lightsBuffer;
    private FrameBuffer sceneBuffer;
    private Texture texture;
    private int gwi, ghi;
    
    public LightRenderer(World world, GameRenderer renderer) {
        world.getWorldBus().register(this);
        this.world = world;
        this.renderer = renderer;
        resize();
    }
    
    @EventSubscription
    public void event2(RendererEvents.ResizeWorldRendererEvent ev) {
        resize();
    }
    
    public void enterLitScene() {
        sceneBuffer.begin();
        ScreenUtils.clear(0, 0, 0, 0);
    }
    
    public void resize() {
        if (lightsBuffer != null) {
            this.lightsBuffer.dispose();
        }
        this.lightsBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);//Hmmmm, width and height, ...?!
        if (sceneBuffer != null) {
            this.sceneBuffer.dispose();
        }
        this.sceneBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }
    
    public void exitAndRenderLitScene() {
        sceneBuffer.end();
        Camera cam = renderer.getWorldView().getCamera();
        SpriteBatch batch = renderer.getSpriteBatch();
        batch.setColor(1, 1, 1, 1);//TODO consider some form of batch resetting?
        
        int xi = Tile.toGlobalTile(cam.position.x - cam.viewportWidth / 2) - extraLightRadius;
        int yi = Tile.toGlobalTile(cam.position.y - cam.viewportHeight / 2) - extraLightRadius;
        int wi = Mathf.ceili(cam.viewportWidth);
        int hi = Mathf.ceili(cam.viewportHeight);
        try {
            new PixelPointLightTask2(world, (pix) -> {
                if (texture != null) {
                    texture.dispose();
                }
                texture = new Texture(pix);
                texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                gwi = texture.getWidth();
                ghi = texture.getHeight();
                pix.dispose();
            }, xi, yi, wi + 2 * extraLightRadius, hi + 2 * extraLightRadius).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        
        this.lightsBuffer.begin();//This framebuffer is good because places where the light is not yet calculated will be pitch black
        {
            ScreenUtils.clear(0, 0, 0, 0);
            if (texture != null) {
                renderer.setAdditiveBlending();
                batch.begin();
                batch.draw(texture, xi, yi, gwi, ghi);
                batch.end();
            }
        }
        this.lightsBuffer.end();
        this.sceneBuffer.begin();
        renderer.applyViewport();
        renderer.setMultiplicativeBlending();
        batch.begin();
        batch.draw(this.lightsBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.lightsBuffer.getWidth(), this.lightsBuffer.getHeight(), false, true);
        batch.end();
        sceneBuffer.end();
        renderer.setDefaultBlending();
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
}
