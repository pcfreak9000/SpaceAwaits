package de.pcfreak9000.spaceawaits.tileworld.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.WorldManager;
import de.pcfreak9000.spaceawaits.tileworld.WorldRenderInfo;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class LightCalculator extends IteratingSystem {
    
    private static final ComponentMapper<LightComponent> lMapper = ComponentMapper.getFor(LightComponent.class);
    
    private WorldRenderInfo info;
    
    private WorldManager wmgr;
    
    private FrameBuffer lightsBuffer;
    
    public LightCalculator() {
        super(Family.all(LightComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void event(WorldEvents.SetWorldEvent ev) {
        this.info = ev.worldMgr.getRenderInfo();
        this.wmgr = ev.worldMgr;
        Camera cam = ev.worldMgr.getRenderInfo().getCamera();
        resize(cam.viewportWidth, cam.viewportHeight);
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
        Camera cam = info.getCamera();
        int xi = Tile.toGlobalTile(cam.position.x - cam.viewportWidth / 2) - extraLightRadius;
        int yi = Tile.toGlobalTile(cam.position.y - cam.viewportHeight / 2) - extraLightRadius;
        int wi = Mathf.ceili(cam.viewportWidth / Tile.TILE_SIZE);
        int hi = Mathf.ceili(cam.viewportHeight / Tile.TILE_SIZE);
        try {
            new PixelPointLightTask2(AmbientLightProvider.constant(Color.WHITE), wmgr.getWorldAccess(), (pix) -> {
                if (texture != null) {
                    texture.dispose();//TODO dispose when this region is deleted/unloaded
                }
                texture = new Texture(pix);
                texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
                gwi = texture.getWidth();
                ghi = texture.getHeight();
                pix.dispose();
            }, xi, yi, wi + 2 * extraLightRadius, hi + 2 * extraLightRadius).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        SpriteBatch batch = info.getSpriteBatch();
        this.lightsBuffer.begin();//This framebuffer is good because places where the light is not yet calculated will be pitch black
        {
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            info.setAdditiveBlending();
            batch.begin();
            if (texture != null) {
                batch.draw(texture, Tile.TILE_SIZE * xi, Tile.TILE_SIZE * yi, gwi * Tile.TILE_SIZE,
                        ghi * Tile.TILE_SIZE);
            }
            batch.end();
        }
        this.lightsBuffer.end();
        info.applyViewport();
        info.setMultiplicativeBlending();
        batch.begin();
        batch.draw(this.lightsBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.lightsBuffer.getWidth(), this.lightsBuffer.getHeight(), false, true);
        batch.end();
        info.setDefaultBlending();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
