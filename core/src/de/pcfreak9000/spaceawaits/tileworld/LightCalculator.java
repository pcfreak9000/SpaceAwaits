package de.pcfreak9000.spaceawaits.tileworld;

import java.util.Iterator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class LightCalculator extends IteratingSystem {
    /*
     * - Tiles in aktuellem Kameraausschnitt - boolean[][] mit Lichtquellen? - Licht
     * propagieren - Rendertexture aufbauen/rendern
     * 
     */
    private static final float LIGHT_SIZE = Tile.TILE_SIZE / 1;
    private static final int LIGHT_RADIUS_EXT = 20;
    
    private static final ComponentMapper<LightComponent> lMapper = ComponentMapper.getFor(LightComponent.class);
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    private World world;
    private WorldRenderInfo info;
    
    private FrameBuffer lightsBuffer;
    
    public LightCalculator() {
        super(Family.all(LightComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void event(WorldEvents.SetWorldEvent ev) {
        this.info = ev.worldMgr.getRenderInfo();
        this.world = ev.worldNew;
        Camera cam = ev.worldMgr.getRenderInfo().getCamera();
        resize(cam.viewportWidth, cam.viewportHeight);
    }
    
    public void resize(float widthf, float heightf) {
        if (lightsBuffer != null) {
            this.lightsBuffer.dispose();
        }
        this.lightsBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);//Hmmmm, width and height, ...?!
    }
    
    private Array<Disposable> disposables = new Array<>();
    
    @Override
    public void update(float deltaTime) {
        Camera cam = info.getCamera();
        SpriteBatch batch = info.getSpriteBatch();
        this.lightsBuffer.begin();
        {
            Gdx.gl.glClearColor(0, 0, 0, 0);//Move this to a setting or do ambient 
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            info.setAdditiveBlending();
            batch.begin();
            for (Entity e : getEntities()) {
                Light light = lMapper.get(e).light;
                light.drawLight(batch, world);
                if (light instanceof Disposable) {
                    disposables.add((Disposable) light);
                }
            }
            batch.end();
            for (Iterator<Disposable> it = disposables.iterator(); it.hasNext();) {
                it.next().dispose();
                it.remove();//This is ugly...
            }
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
