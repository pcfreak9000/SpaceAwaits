package de.pcfreak9000.spaceawaits.world.light;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
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
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class LightCalculator extends IteratingSystem implements Disposable {
    
    private World world;
    
    private FrameBuffer lightsBuffer;
    
    public LightCalculator(World world) {
        super(Family.all().get());
        world.getWorldBus().register(this);
        this.world = world;
        resize(0, 0);//If the world is constructed asynchronously, this will fail
    }
    
    @EventSubscription
    public void event2(RendererEvents.ResizeWorldRendererEvent ev) {
        resize(ev.renderer.getView().getCamera().viewportWidth, ev.renderer.getView().getCamera().viewportHeight);
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
        Camera cam = SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().getView().getCamera();
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
        SpriteBatch batch = SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().getSpriteBatch();
        batch.setColor(1, 1, 1, 1);//TODO consider some form of batch resetting?
        this.lightsBuffer.begin();//This framebuffer is good because places where the light is not yet calculated will be pitch black
        {
            ScreenUtils.clear(0, 0, 0, 0);
            SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().setAdditiveBlending();
            batch.begin();
            if (texture != null) {
                batch.draw(texture, xi, yi, gwi, ghi);
            }
            batch.end();
        }
        this.lightsBuffer.end();
        SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().applyViewport();
        SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().setMultiplicativeBlending();
        batch.begin();
        batch.draw(this.lightsBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.lightsBuffer.getWidth(), this.lightsBuffer.getHeight(), false, true);
        batch.end();
        SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().setDefaultBlending();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
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
