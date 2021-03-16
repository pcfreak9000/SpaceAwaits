package de.pcfreak9000.spaceawaits.world.ecs.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

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
    private Camera cam;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.b = SpaceAwaits.getSpaceAwaits().worldRenderer.getSpriteBatch();
        this.cam = SpaceAwaits.getSpaceAwaits().worldRenderer.getCamera();
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
        if (transformMapper.has(entity)) {
            Vector2 p = transformMapper.get(entity).position;
            rec.sprite.setPosition(p.x, p.y);
        }
        float wh = 0.5f * rec.sprite.getWidth() * rec.sprite.getScaleX();
        float hh = 0.5f * rec.sprite.getHeight() * rec.sprite.getScaleY();
        float mx = rec.sprite.getX() + wh;
        float my = rec.sprite.getY() + hh;
        if (!cam.frustum.boundsInFrustum(mx, my, 0, wh, hh, 0)) {
            return;
        }
        rec.action.act(rec.sprite);
        rec.sprite.draw(b);
    }
}
