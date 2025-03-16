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
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.event.Event;
import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.render.strategy.AbstractRenderStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.IRenderStrategy;

public class RenderSystem extends EntitySystem implements EntityListener, Disposable, RenderSystemMarker {
    
    private static final Family FAMILY = Family.all(RenderComponent.class).get();
    
    private static final Comparator<Entity> COMPARATOR = (e1, e2) -> {
        RenderComponent r1 = Components.RENDER.get(e1);
        RenderComponent r2 = Components.RENDER.get(e2);
        float maj = r1.getLayer() - r2.getLayer();
        // if (maj == 0) {
        // int min = r1.renderStratId.hashCode() - r2.renderStratId.hashCode();
        // return min;
        // }
        return (int) Math.signum(maj);
    };
    
    public static final class RegisterRenderStrategiesEvent extends Event {
        private final OrderedSet<IRenderStrategy> renderStrategies;
        public final Engine world;
        public final GameScreen renderer;
        
        public RegisterRenderStrategiesEvent(OrderedSet<IRenderStrategy> rendstrat, Engine world, GameScreen renderer) {
            this.renderStrategies = rendstrat;
            this.world = world;
            this.renderer = renderer;
        }
        
        public void addStrategy(IRenderStrategy strat) {
            this.renderStrategies.add(strat);
        }
    }
    
    private static final float BEGIN_LIGHT_LAYER = RenderLayers.BEGIN_LIGHT;
    private static final float END_LIGHT_LAYER = RenderLayers.END_LIGHT;
    
    private final SystemCache<CameraSystem> camsys = new SystemCache<>(CameraSystem.class);
    
    private final OrderedSet<IRenderStrategy> renderStrategies;
    private Array<Entity> entities;
    private LightRenderer lightRenderer;
    private GameScreen renderer;
    
    private FrameBuffer sceneBuffer;
    private SpriteBatchImpr batch;
    
    private boolean forceSort = false;
    
    private boolean dolightsetting = true;
    
    public RenderSystem(Engine engine, GameScreen renderer) {
        this.entities = new Array<>();
        this.renderStrategies = new OrderedSet<>();
        this.lightRenderer = new LightRenderer(renderer);
        this.renderer = renderer;
        this.batch = renderer.getRenderHelper().getSpriteBatch();
        resize();
        SpaceAwaits.BUS.post(new RegisterRenderStrategiesEvent(this.renderStrategies, engine, renderer));
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
        // this.sceneBuffer.getColorBufferTexture().setAnisotropicFilter(16f);
        // this.sceneBuffer.getColorBufferTexture().setFilter(TextureFilter.MipMap,
        // TextureFilter.Linear);
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.lightRenderer.addedToEngineInternal(engine);
        engine.addEntityListener(FAMILY, this);
        ImmutableArray<Entity> engineEntities = engine.getEntitiesFor(FAMILY);
        this.entities.ensureCapacity(Math.max(0, engineEntities.size() - this.entities.size));
        for (Entity e : engineEntities) {
            addEntityInternal(e);
        }
        forceLayerSort();
        for (IRenderStrategy strat : this.renderStrategies) {
            if (strat instanceof AbstractRenderStrategy) {
                AbstractRenderStrategy ast = (AbstractRenderStrategy) strat;
                ast.addedToEngineInternal(engine);
            }
        }
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        this.lightRenderer.removedFromEngineInternal(engine);
        for (IRenderStrategy strat : this.renderStrategies) {
            if (strat instanceof AbstractRenderStrategy) {
                AbstractRenderStrategy ast = (AbstractRenderStrategy) strat;
                ast.removedFromEngineInternal(engine);
            }
        }
        super.removedFromEngine(engine);
        for (Entity e : entities) {
            removeEntityPrep(e);
        }
        this.entities.clear();
        this.entities.shrink();
        engine.removeEntityListener(this);
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
        for (IRenderStrategy r : this.renderStrategies) {
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
            this.entities.sort(COMPARATOR.thenComparing(COMPARATOR));
            this.forceSort = false;
        }
    }
    
    @Override
    public void update(float deltaTime) {
        boolean dolight = dolightsetting;
        super.update(deltaTime);
        renderer.getRenderHelper().getFBOStack().push(sceneBuffer);
        ScreenUtils.clear(0, 0, 0, 0);
        IRenderStrategy last = null;
        float lastLayer = Float.NEGATIVE_INFINITY;
        boolean endedLight = false;
        sortIfNecessary();
        for (Entity e : entities) {
            RenderComponent rc = Components.RENDER.get(e);
            if (!rc.enabled || (rc.considerAsGui && !renderer.isShowGuiElements())) {
                continue;
            }
            boolean startLight = lastLayer < BEGIN_LIGHT_LAYER && rc.getLayer() >= BEGIN_LIGHT_LAYER && dolight;
            for (IRenderStrategy dec : rc.renderStrategies) {
                if (dec.considerGui() && !renderer.isShowGuiElements()) {
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
                if (lastLayer < END_LIGHT_LAYER && rc.getLayer() >= END_LIGHT_LAYER && dolight) {
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
        if (!endedLight && dolight) {
            lightRenderer.exitAndRenderLitScene();
        }
        renderer.getRenderHelper().getFBOStack().pop(sceneBuffer);
        renderer.getRenderHelper().applyViewport();
        batch.setDefaultBlending();
        batch.setColor(Color.WHITE);
        Camera cam = camsys.get(getEngine()).getCamera();
        batch.begin();
        batch.draw(this.sceneBuffer.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                this.sceneBuffer.getWidth(), this.sceneBuffer.getHeight(), false, true);
        batch.end();
    }
    
    public void setDoLight(boolean b) {
        this.dolightsetting = b;
    }
    
    @Override
    public void dispose() {
        this.lightRenderer.dispose();
        this.sceneBuffer.dispose();
        for (IRenderStrategy irs : this.renderStrategies) {
            if (irs instanceof Disposable) {
                Disposable d = (Disposable) irs;
                d.dispose();
            }
        }
    }
}
