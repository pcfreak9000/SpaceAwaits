package de.pcfreak9000.spaceawaits.world.render;

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
import com.badlogic.gdx.utils.ObjectMap;

public class RenderSystem extends EntitySystem implements EntityListener {
    
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
    
    private ObjectMap<String, IRenderDecorator> renderDecorators = new ObjectMap<>();
    
    private Array<Entity> entities = new Array<>();
    
    //TODO use a GameRegistry instead for RenderDecorators?
    
    public void registerRenderDecorator(String id, IRenderDecorator dec) {
        renderDecorators.put(id, dec);
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
        IRenderDecorator renderDecorator = this.renderDecorators.get(rc.renderDecoratorId);
        if (renderDecorator == null) {
            throw new IllegalStateException("No such IRenderDecorator: " + rc.renderDecoratorId);
        }
        boolean matches = renderDecorator.getFamily().matches(entity);
        if (!matches) {
            throw new IllegalStateException("Entity does not have the right components for this render decorator");
        }
        rc.renderDecorator = renderDecorator;
        this.entities.add(entity);
        if (renderDecorator instanceof EntityListener) {
            EntityListener el = (EntityListener) renderDecorator;
            el.entityAdded(entity);
        }
    }
    
    private void removeEntityPrep(Entity entity) {
        RenderComponent rc = rMapper.get(entity);
        IRenderDecorator dec = rc.renderDecorator;
        if (dec instanceof EntityListener) {
            EntityListener el = (EntityListener) dec;
            el.entityRemoved(entity);
        }
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        IRenderDecorator last = null;
        for (Entity e : entities) {
            RenderComponent rc = rMapper.get(e);
            IRenderDecorator dec = rc.renderDecorator;
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
}
