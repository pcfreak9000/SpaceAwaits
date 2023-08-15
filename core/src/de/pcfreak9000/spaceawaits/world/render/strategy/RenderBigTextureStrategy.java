package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.render.GameScreen;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderBigTextureComponent;

public class RenderBigTextureStrategy extends AbstractRenderStrategy {
    
    private GameScreen renderer;
    
    public RenderBigTextureStrategy(GameScreen renderer) {
        super(Family.all(RenderBigTextureComponent.class, TransformComponent.class).get());
        this.b = renderer.getSpriteBatch();
        this.cam = renderer.getCamera();
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
        RenderBigTextureComponent rec = Components.RENDER_BIG_TEXTURE.get(entity);//Maybe make this a RENDER_SELF_COMPONENT or something and use an interface with render(...)
        TransformComponent tc = Components.TRANSFORM.get(entity);
        Vector2 p = tc.position;
        if (!cam.frustum.sphereInFrustum(p.x, p.y, 0, Math.max(rec.width, rec.height))) {
            return;
        }
        if (rec.color != null) {
            b.setColor(rec.color);
        } else {
            b.setColor(Color.WHITE);
        }
        rec.texture.render(b, p.x, p.y, rec.width, rec.height);
        //        b.draw(rec.texture.getRegion(), p.x, p.y, tc.originx, tc.originy, rec.width, rec.height, 1, 1,
        //                MathUtils.radiansToDegrees * tc.rotation);
    }
}
