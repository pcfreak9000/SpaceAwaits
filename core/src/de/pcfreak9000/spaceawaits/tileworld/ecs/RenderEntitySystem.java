package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;

public class RenderEntitySystem extends IteratingSystem {
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<RenderEntityComponent> renderMapper = ComponentMapper
            .getFor(RenderEntityComponent.class);
    
    public RenderEntitySystem() {
        super(Family.all(RenderEntityComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    private SpriteBatch b;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.b = svwe.worldMgr.getRenderInfo().getSpriteBatch();
    }
    
    @Override
    public void update(float deltaTime) {
        this.b.begin();
        super.update(deltaTime);
        this.b.end();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RenderEntityComponent rec = renderMapper.get(entity);
        if (transformMapper.has(entity)) {//TODO visibility (frustum) checks
            Vector2 p = transformMapper.get(entity).position;
            rec.sprite.setPosition(p.x, p.y);
        }
        rec.sprite.draw(b);
    }
}
