package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class RenderRenderableStrategy extends AbstractRenderStrategy {
    
    private GameScreen renderer;
    private SpriteBatch b;
    private Camera cam;
    
    public RenderRenderableStrategy(GameScreen renderer) {
        super(Family.all(RenderRenderableComponent.class, TransformComponent.class).get());
        this.b = renderer.getSpriteBatch();
        this.renderer = renderer;
    }
    
    @Override
    public void begin() {
        renderer.applyViewport();
        this.b.begin();
        this.cam = getEngine().getSystem(CameraSystem.class).getCamera();
    }
    
    @Override
    public void end() {
        this.b.end();
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
        RenderRenderableComponent rec = Components.RENDER_RENDERABLE.get(entity);
        TransformComponent tc = Components.TRANSFORM.get(entity);
        Vector2 p = tc.position;
        if (rec.dofrustumcheck && !cam.frustum.sphereInFrustum(p.x, p.y, 0, Math.max(rec.width, rec.height))) {
            return;
        }
        if (rec.color != null) {
            b.setColor(rec.color);
        } else {
            b.setColor(Color.WHITE);
        }
        rec.renderable.render(b, p.x, p.y, tc.rotoffx, tc.rotoffy, rec.width, rec.height, 1, 1,
                MathUtils.radiansToDegrees * tc.rotation);
    }
}
