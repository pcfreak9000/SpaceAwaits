package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

public class RenderTextureStrategy extends AbstractRenderStrategy {
    
    private GameRenderer renderer;
    
    public RenderTextureStrategy(GameRenderer renderer) {
        super(Family.all(RenderTextureComponent.class, TransformComponent.class).get());
        this.b = renderer.getSpriteBatch();
        this.cam = renderer.getCurrentView().getCamera();
        this.renderer = renderer;
    }
    
    private SpriteBatch b;
    private Camera cam;
    
    @Override
    public void begin() {
        renderer.applyViewport();
        this.b.begin();
    }
    
    @Override
    public void end() {
        this.b.end();
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
        RenderTextureComponent rec = Components.RENDER_TEXTURE.get(entity);
        Vector2 p = Components.TRANSFORM.get(entity).position;
        float wh = 0.5f * rec.width;
        float hh = 0.5f * rec.height;
        float mx = p.x + wh;
        float my = p.y + hh;
        if (!cam.frustum.boundsInFrustum(mx, my, 0, wh, hh, 0)) {
            return;
        }
        if (rec.color != null) {
            b.setColor(rec.color);
        } else {
            b.setColor(Color.WHITE);
        }
        b.draw(rec.texture.getRegion(), p.x, p.y, rec.width, rec.height);
    }
}
