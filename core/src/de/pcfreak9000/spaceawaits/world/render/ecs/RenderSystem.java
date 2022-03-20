package de.pcfreak9000.spaceawaits.world.render.ecs;

import java.util.Comparator;
import java.util.Objects;

import com.badlogic.ashley.core.ComponentMapper;
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
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.render.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.world.render.strategy.AbstractRenderStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.IRenderStrategy;

public class RenderSystem extends EntitySystem implements EntityListener, Disposable, RenderSystemMarker {
    
    private static final Family FAMILY = Family.all(RenderComponent.class).get();
    
    private static final ComponentMapper<RenderComponent> rMapper = ComponentMapper.getFor(RenderComponent.class);
    
    private static final Comparator<Entity> COMPARATOR = (e1, e2) -> {
        RenderComponent r1 = rMapper.get(e1);
        RenderComponent r2 = rMapper.get(e2);
        float maj = r1.layer - r2.layer;
        if (maj == 0) {
            int min = r1.renderStratId.hashCode() - r2.renderStratId.hashCode();
            return min;
        }
        return (int) Math.signum(maj);
    };
    
    public static final class RegisterRenderStrategiesEvent extends Event {
        public final GameRegistry<IRenderStrategy> renderStrategies;
        public final World world;
        public final GameRenderer renderer;
        
        public RegisterRenderStrategiesEvent(GameRegistry<IRenderStrategy> rendstrat, World world,
                GameRenderer renderer) {
            this.renderStrategies = rendstrat;
            this.world = world;
            this.renderer = renderer;
        }
    }
    
    private static final float BEGIN_LIGHT_LAYER = 0;//TODO make light disableable
    private static final float END_LIGHT_LAYER = 100;
    
    private final GameRegistry<IRenderStrategy> renderStrategies;
    private Array<Entity> entities;
    private LightRenderer lightRenderer;
    private GameRenderer renderer;
    
    private FrameBuffer sceneBuffer;
    private SpriteBatchImpr batch;
    
    public RenderSystem(World world, GameRenderer renderer) {
        world.getWorldBus().register(this);
        this.entities = new Array<>();
        this.renderStrategies = new GameRegistry<>();
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
        this.entities.sort(COMPARATOR);
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
        this.entities.sort(COMPARATOR);
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        removeEntityPrep(entity);
        this.entities.removeValue(entity, true);
    }
    
    private void addEntityInternal(Entity entity) {
        RenderComponent rc = rMapper.get(entity);
        Objects.requireNonNull(rc.renderStratId);
        IRenderStrategy renderStrategy = this.renderStrategies.get(rc.renderStratId);
        if (renderStrategy == null) {
            throw new IllegalStateException("No such IRenderStrategy: " + rc.renderStratId);
        }
        boolean matches = renderStrategy.getFamily().matches(entity);
        if (!matches) {
            throw new IllegalStateException("Entity does not have the right components for the render strategy '"
                    + rc.renderStratId + "': " + entity);
        }
        rc.renderStrategy = renderStrategy;
        this.entities.add(entity);
        if (renderStrategy instanceof EntityListener) {
            EntityListener el = (EntityListener) renderStrategy;
            el.entityAdded(entity);
        }
    }
    
    private void removeEntityPrep(Entity entity) {
        RenderComponent rc = rMapper.get(entity);
        IRenderStrategy dec = rc.renderStrategy;
        if (dec instanceof EntityListener) {
            EntityListener el = (EntityListener) dec;
            el.entityRemoved(entity);
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
        for (Entity e : entities) {
            RenderComponent rc = rMapper.get(e);
            if (!rc.enabled || (rc.considerAsGui && !renderer.showGui())) {
                continue;
            }
            IRenderStrategy dec = rc.renderStrategy;
            boolean startLight = lastLayer < BEGIN_LIGHT_LAYER && rc.layer >= BEGIN_LIGHT_LAYER;
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
            if (lastLayer < END_LIGHT_LAYER && rc.layer >= END_LIGHT_LAYER) {
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
            lastLayer = rc.layer;
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
        Camera cam = this.renderer.getWorldView().getCamera();
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
