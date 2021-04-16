package de.pcfreak9000.spaceawaits.world.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldManager;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class LightCalculator extends IteratingSystem {
    
    private static final ComponentMapper<LightComponent> lMapper = ComponentMapper.getFor(LightComponent.class);
    
    private WorldManager wmgr;
    
    private FrameBuffer lightsBuffer;
    
    public LightCalculator() {
        super(Family.all(LightComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void event2(RendererEvents.ResizeWorldRendererEvent ev) {
        resize(ev.renderer.getCamera().viewportWidth, ev.renderer.getCamera().viewportHeight);
    }
    
    @EventSubscription
    public void event(WorldEvents.SetWorldEvent ev) {
        this.wmgr = ev.worldMgr;
        Camera cam = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getCamera();
        resize(cam.viewportWidth, cam.viewportHeight);
    }
    
    @EventSubscription
    private void event3(CoreEvents.ExitEvent ex) {
        Logger.getLogger(getClass()).debug("Disposing...");
        if (this.texture != null) {
            this.texture.dispose();
            this.texture = null;
        }
        if (lightsBuffer != null) {
            this.lightsBuffer.dispose();
        }
    }
    
    public void resize(float widthf, float heightf) {
        if (lightsBuffer != null) {
            this.lightsBuffer.dispose();
        }
        this.lightsBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);//Hmmmm, width and height, ...?!
    }
    
    private Texture texture;
    private int gwi, ghi;
    
    private static final int extraLightRadius = 25;
    
    @Override
    public void update(float deltaTime) {
        Camera cam = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getCamera();
        int xi = Tile.toGlobalTile(cam.position.x - cam.viewportWidth / 2) - extraLightRadius;
        int yi = Tile.toGlobalTile(cam.position.y - cam.viewportHeight / 2) - extraLightRadius;
        int wi = Mathf.ceili(cam.viewportWidth);
        int hi = Mathf.ceili(cam.viewportHeight);
        try {
            new PixelPointLightTask2(wmgr.getWorldAccess(), (pix) -> {
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
        SpriteBatch batch = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getSpriteBatch();
        this.lightsBuffer.begin();//This framebuffer is good because places where the light is not yet calculated will be pitch black
        {
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().setAdditiveBlending();
            batch.begin();
            if (texture != null) {
                batch.draw(texture, xi, yi, gwi, ghi);
            }
            batch.end();
        }
        this.lightsBuffer.end();
        SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().applyViewport();
        SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().setMultiplicativeBlending();
        batch.begin();
        batch.draw(this.lightsBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.lightsBuffer.getWidth(), this.lightsBuffer.getHeight(), false, true);
        batch.end();
        SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().setDefaultBlending();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
