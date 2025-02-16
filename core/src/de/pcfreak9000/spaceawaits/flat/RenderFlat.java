package de.pcfreak9000.spaceawaits.flat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.strategy.AbstractRenderStrategy;

public class RenderFlat extends AbstractRenderStrategy {
    private SpriteBatch batch;
    
    public RenderFlat(GameScreen rend) {
        super(Family.all(TransformComponent.class).get());
        this.batch = rend.getRenderHelper().getSpriteBatch();
    }
    
    @Override
    public boolean considerGui() {
        return false;
    }
    
    @Override
    public void begin() {
        batch.begin();
    }
    
    @Override
    public void end() {
        batch.end();
    }
    
    @Override
    public void render(Entity e, float dt) {
        TransformComponent tc = Components.TRANSFORM.get(e);
        float statsx = tc.position.x;
        float statsy = tc.position.y;
        float x = statsx;
        float y = statsy;
        batch.draw(CoreRes.WHITE, x, y, 1, 1);
    }
}
