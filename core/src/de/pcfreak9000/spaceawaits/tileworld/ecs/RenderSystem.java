package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.tileworld.WorldRenderer;

public class RenderSystem extends IteratingSystem implements EntityListener {
    
    private final ComponentMapper<RenderComponent> renderMapper = ComponentMapper.getFor(RenderComponent.class);
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    private WorldRenderer renderer;
    
    public RenderSystem(WorldRenderer renderer) {
        super(Family.all(RenderComponent.class).get());
        this.renderer = renderer;
    }
    
    @Override
    public void entityAdded(Entity entity) {
        registerRenderedEntity(entity);
    }
    
    private void registerRenderedEntity(Entity entity) {
        RenderComponent rc = this.renderMapper.get(entity);
        this.renderer.add(rc.sprite);
        //        if (rc.light != null) {
        //            this.renderer.addLight(rc.light);
        //        }
        //sync the rendering transform to the actual transform
        if (transformMapper.has(entity)) {
            rc.sprite.setTransform(this.transformMapper.get(entity).transform);
            //            if (rc.light != null && rc.light instanceof SimpleSprite) {
            //                ((SimpleSprite) rc.light).getTransform().setParent(this.transformMapper.get(entity).transform);
            //            }
        }
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        RenderComponent rc = this.renderMapper.get(entity);
        this.renderer.remove(rc.sprite);
        //        if (rc.light != null) {
        //            this.renderer.removeLight(rc.light);
        //        }
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        //add already registered entities that are not noticed by the EntityListener
        for (Entity e : this.getEntities()) {
            registerRenderedEntity(e);
        }
        engine.addEntityListener(getFamily(), this);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }
    
}
