package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.tile.BreakTileProgress;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.BreakingTilesComponent;

public class RenderTileBreakingStrategy extends AbstractRenderStrategy {
    
    private static final ComponentMapper<BreakingTilesComponent> MAPPER = ComponentMapper
            .getFor(BreakingTilesComponent.class);
    
    public RenderTileBreakingStrategy(GameRenderer renderer) {
        super(Family.all(BreakingTilesComponent.class).get());
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
    public void render(Entity e, float dt) {
        BreakingTilesComponent c = MAPPER.get(e);
        for (BreakTileProgress t : c.breaktiles.values()) {
            int tx = t.getX();
            int ty = t.getY();
            if (!cam.frustum.boundsInFrustum(tx + 0.5f, ty + 0.5f, 0, 0.5f, 0.5f, 0)) {
                return;
            }
            int len = CoreRes.BREAK_OVERLAY.length;
            int index = Mathf.floori(Mathf.clamp(t.getProgress(), 0, 0.9999f) * len);
            float f = t.getLayer() == TileLayer.Front ? 1 : 0.8f;
            b.setColor(f, f, f, 1);
            b.draw(CoreRes.BREAK_OVERLAY[index].getRegion(), tx, ty, 1f, 1f);
        }
    }
    
}
