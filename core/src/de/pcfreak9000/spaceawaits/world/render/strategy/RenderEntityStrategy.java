package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderEntityComponent;

public class RenderEntityStrategy extends AbstractRenderStrategy {
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<RenderEntityComponent> renderMapper = ComponentMapper
            .getFor(RenderEntityComponent.class);
    
    
    public RenderEntityStrategy(GameRenderer renderer) {
        super(Family.all(RenderEntityComponent.class).get());
        this.b = renderer.getSpriteBatch();
        this.cam = renderer.getCurrentView().getCamera();
    }
    
    private SpriteBatch b;
    private Camera cam;
    
    @Override
    public void begin() {
        this.b.begin();
    }
    
    @Override
    public void end() {
        this.b.end();
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
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
