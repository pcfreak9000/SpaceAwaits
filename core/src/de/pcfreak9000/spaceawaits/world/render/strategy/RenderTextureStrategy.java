package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

public class RenderTextureStrategy extends AbstractRenderStrategy {
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<RenderTextureComponent> renderMapper = ComponentMapper
            .getFor(RenderTextureComponent.class);
    
    public RenderTextureStrategy(GameRenderer renderer) {
        super(Family.all(RenderTextureComponent.class, TransformComponent.class).get());
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
        RenderTextureComponent rec = renderMapper.get(entity);
        Vector2 p = transformMapper.get(entity).position;
        float wh = 0.5f * rec.width;
        float hh = 0.5f * rec.height;
        float mx = p.x + wh;
        float my = p.y + hh;
        if (!cam.frustum.boundsInFrustum(mx, my, 0, wh, hh, 0)) {
            return;
        }
        if (rec.color != null) {
            b.setColor(rec.color);
        }
        b.draw(rec.texture.getRegion(), p.x, p.y, rec.width, rec.height);
    }
}
