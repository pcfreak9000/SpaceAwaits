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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.event.Event;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.strategy.IRenderStrategy;

public class RenderSystem extends EntitySystem implements EntityListener, Disposable {
    
    private static final Family FAMILY = Family.all(RenderComponent.class).get();
    
    private static final ComponentMapper<RenderComponent> rMapper = ComponentMapper.getFor(RenderComponent.class);
    
    private static final Comparator<Entity> COMPARATOR = (e1, e2) -> {
        RenderComponent r1 = rMapper.get(e1);
        RenderComponent r2 = rMapper.get(e2);
        int maj = r1.layer - r2.layer;
        if (maj == 0) {
            int min = r1.renderDecoratorId.hashCode() - r2.renderDecoratorId.hashCode();
            return min;
        }
        return maj;
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
    
    private final GameRegistry<IRenderStrategy> renderStrategies;
    private Array<Entity> entities;
    
    public RenderSystem(World world, GameRenderer renderer) {
        this.entities = new Array<>();
        this.renderStrategies = new GameRegistry<>();
        SpaceAwaits.BUS.post(new RegisterRenderStrategiesEvent(this.renderStrategies, world, renderer));
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
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
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
        Objects.requireNonNull(rc.renderDecoratorId);
        IRenderStrategy renderStrategy = this.renderStrategies.get(rc.renderDecoratorId);
        if (renderStrategy == null) {
            throw new IllegalStateException("No such IRenderStrategyr: " + rc.renderDecoratorId);
        }
        boolean matches = renderStrategy.getFamily().matches(entity);
        if (!matches) {
            throw new IllegalStateException("Entity does not have the right components for this render decorator");
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
        IRenderStrategy last = null;
        for (Entity e : entities) {
            RenderComponent rc = rMapper.get(e);
            IRenderStrategy dec = rc.renderStrategy;
            if (dec != last) {
                if (last != null) {
                    last.end();
                }
                dec.begin();
                last = dec;
            }
            dec.render(e, deltaTime);
        }
        if (last != null) {
            last.end();
        }
    }
    
    @Override
    public void dispose() {
        for (IRenderStrategy irs : this.renderStrategies.getAll()) {
            if (irs instanceof Disposable) {
                Disposable d = (Disposable) irs;
                d.dispose();
            }
        }
    }
}
