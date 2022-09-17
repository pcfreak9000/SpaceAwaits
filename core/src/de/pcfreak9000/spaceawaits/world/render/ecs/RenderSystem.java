package de.pcfreak9000.spaceawaits.world.render.ecs;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.event.Event;
import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.render.GameScreen;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.render.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.world.render.strategy.AbstractRenderStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.IRenderStrategy;

public class RenderSystem extends EntitySystem implements EntityListener, Disposable, RenderSystemMarker {
    
    private static final Family FAMILY = Family.all(RenderComponent.class).get();
    
    private static final Comparator<Entity> COMPARATOR = (e1, e2) -> {
        RenderComponent r1 = Components.RENDER.get(e1);
        RenderComponent r2 = Components.RENDER.get(e2);
        float maj = r1.getLayer() - r2.getLayer();
        //        if (maj == 0) {
        //            int min = r1.renderStratId.hashCode() - r2.renderStratId.hashCode();
        //            return min;
        //        }
        return (int) Math.signum(maj);
    };
    
    public static final class RegisterRenderStrategiesEvent extends Event {
        public final Registry<IRenderStrategy> renderStrategies;
        public final World world;
        public final GameScreen renderer;
        
        public RegisterRenderStrategiesEvent(Registry<IRenderStrategy> rendstrat, World world, GameScreen renderer) {
            this.renderStrategies = rendstrat;
            this.world = world;
            this.renderer = renderer;
        }
    }
    
    private static final float BEGIN_LIGHT_LAYER = RenderLayers.BEGIN_LIGHT;//TODO make light disableable
    private static final float END_LIGHT_LAYER = RenderLayers.END_LIGHT;
    
    private final Registry<IRenderStrategy> renderStrategies;
    private Array<Entity> entities;
    private LightRenderer lightRenderer;
    private GameScreen renderer;
    
    private FrameBuffer sceneBuffer;
    private SpriteBatchImpr batch;
    
    private boolean forceSort = false;
    
    public RenderSystem(World world, GameScreen renderer) {
        world.getWorldBus().register(this);
        this.entities = new Array<>();
        this.renderStrategies = new Registry<>();
        this.lightRenderer = new LightRenderer(world, renderer);
        this.renderer = renderer;
        this.batch = renderer.getSpriteBatch();//new SpriteBatchImpr(100);
        resize();
        SpaceAwaits.BUS.post(new RegisterRenderStrategiesEvent(this.renderStrategies, world, renderer));
    }
    
    @EventSubscription
    public void event2(RendererEvents.ResizeWorldRendererEvent ev) {
        resize();
    }
    
    private void resize() {
        if (sceneBuffer != null) {
            this.sceneBuffer.dispose();
        }
        this.sceneBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(FAMILY, this);
        ImmutableArray<Entity> engineEntities = engine.getEntitiesFor(FAMILY);
        this.entities.ensureCapacity(Math.max(0, engineEntities.size() - this.entities.size));
        for (Entity e : engineEntities) {
            addEntityInternal(e);
        }
        forceLayerSort();
        for (IRenderStrategy strat : this.renderStrategies.getAll()) {
            if (strat instanceof AbstractRenderStrategy) {
                AbstractRenderStrategy ast = (AbstractRenderStrategy) strat;
                ast.addedToEngineInternal(engine);
            }
        }
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        for (IRenderStrategy strat : this.renderStrategies.getAll()) {
            if (strat instanceof AbstractRenderStrategy) {
                AbstractRenderStrategy ast = (AbstractRenderStrategy) strat;
                ast.removedFromEngineInternal(engine);
            }
        }
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
        for (Entity e : entities) {
            removeEntityPrep(e);
        }
        this.entities.clear();
        this.entities.shrink();
    }
    
    @Override
    public void entityAdded(Entity entity) {
        addEntityInternal(entity);
        forceLayerSort();
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        removeEntityPrep(entity);
        this.entities.removeValue(entity, true);
    }
    
    private void addEntityInternal(Entity entity) {
        RenderComponent rc = Components.RENDER.get(entity);
        rc.renSys = this;
        for (IRenderStrategy r : this.renderStrategies.getAll()) {
            if (r.getFamily().matches(entity)) {
                rc.renderStrategies.add(r);
                if (r instanceof EntityListener) {
                    EntityListener el = (EntityListener) r;
                    el.entityAdded(entity);
                }
            }
        }
        this.entities.add(entity);
    }
    
    private void removeEntityPrep(Entity entity) {
        RenderComponent rc = Components.RENDER.get(entity);
        rc.renSys = null;
        for (IRenderStrategy r : rc.renderStrategies) {
            if (r instanceof EntityListener) {
                EntityListener el = (EntityListener) r;
                el.entityRemoved(entity);
            }
        }
        rc.renderStrategies.clear();
    }
    
    public void forceLayerSort() {
        this.forceSort = true;
    }
    
    private void sortIfNecessary() {
        if (forceSort) {
            this.entities.sort(COMPARATOR);
            this.forceSort = false;
        }
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        renderer.getFBOStack().push(sceneBuffer);
        ScreenUtils.clear(0, 0, 0, 0);
        IRenderStrategy last = null;
        float lastLayer = Float.NEGATIVE_INFINITY;
        boolean endedLight = false;
        sortIfNecessary();
        for (Entity e : entities) {
            RenderComponent rc = Components.RENDER.get(e);
            if (!rc.enabled || (rc.considerAsGui && !renderer.showGui())) {
                continue;
            }
            boolean startLight = lastLayer < BEGIN_LIGHT_LAYER && rc.getLayer() >= BEGIN_LIGHT_LAYER;
            for (IRenderStrategy dec : rc.renderStrategies) {
                if (dec.considerGui() && !renderer.showGui()) {
                    continue;
                }
                if (dec != last || startLight) {
                    if (last != null) {
                        last.end();
                    }
                    if (startLight) {
                        lightRenderer.enterLitScene();
                    }
                    dec.begin();
                    last = dec;
                }
                if (lastLayer < END_LIGHT_LAYER && rc.getLayer() >= END_LIGHT_LAYER) {
                    if (last != null) {
                        last.end();
                    }
                    lightRenderer.exitAndRenderLitScene();
                    endedLight = true;
                    if (last != null) {
                        last.begin();
                    }
                }
                dec.render(e, deltaTime);
            }
            lastLayer = rc.getLayer();
        }
        if (last != null) {
            last.end();
        }
        if (!endedLight) {
            lightRenderer.exitAndRenderLitScene();
        }
        renderer.getFBOStack().pop(sceneBuffer);
        renderer.applyViewport();
        batch.setDefaultBlending();
        batch.setColor(Color.WHITE);
        Camera cam = this.renderer.getCamera();
        batch.begin();
        batch.draw(this.sceneBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.sceneBuffer.getWidth(), this.sceneBuffer.getHeight(), false, true);
        batch.end();
    }
    
    @Override
    public void dispose() {
        this.lightRenderer.dispose();
        this.sceneBuffer.dispose();
        for (IRenderStrategy irs : this.renderStrategies.getAll()) {
            if (irs instanceof Disposable) {
                Disposable d = (Disposable) irs;
                d.dispose();
            }
        }
    }
}
