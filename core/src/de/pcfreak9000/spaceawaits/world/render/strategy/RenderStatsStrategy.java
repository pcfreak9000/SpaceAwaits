package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.RenderStatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent.StatData;

public class RenderStatsStrategy extends AbstractRenderStrategy {
    
    private SpriteBatch batch;
    
    public RenderStatsStrategy(GameScreen rend) {
        super(Family.all(TransformComponent.class, StatsComponent.class, RenderStatsComponent.class).get());
        this.batch = rend.getRenderHelper().getSpriteBatch();
    }
    
    @Override
    public boolean considerGui() {
        return true;
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
        StatsComponent hc = Components.STATS.get(e);
        RenderStatsComponent rsc = Components.RENDER_STATS.get(e);
        float statsx = tc.position.x + rsc.xOff;
        float statsy = tc.position.y + rsc.yOff;
        float x = statsx;
        float y = statsy;
        for (StatData st : hc.statDatas.values()) {
            float ratio = st.current / st.max;
            ratio = Mathf.clamp(ratio, 0, 1);
            batch.setColor(Color.DARK_GRAY);
            batch.draw(CoreRes.WHITE, x - 0.05f, y - 0.05f, rsc.width + 0.1f, 0.3f);
            batch.setColor(Color.RED);
            batch.draw(CoreRes.WHITE, x, y, rsc.width, 0.2f);
            if (ratio > 0.01f) {
                batch.setColor(Color.GREEN);
                batch.draw(CoreRes.WHITE, x, y, rsc.width * ratio, 0.2f);
            }
            y += 0.21f;
        }
    }
}
