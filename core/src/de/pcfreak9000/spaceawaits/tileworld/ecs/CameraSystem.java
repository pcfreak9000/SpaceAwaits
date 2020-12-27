package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.WorldRenderInfo;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class CameraSystem extends IteratingSystem {
    
    private WorldRenderInfo render;
    private TileWorld tileWorld;
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    public CameraSystem() {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.render = svwe.worldMgr.getRenderInfo();
        this.tileWorld = svwe.getTileWorldNew();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = this.transformMapper.get(entity);
        //temporary wrap around
        if (tc.position.x < 0) {
            tc.position.add(this.tileWorld.getWorldWidth() * Tile.TILE_SIZE, 0);
        } else if (tc.position.x > this.tileWorld.getWorldWidth() * Tile.TILE_SIZE) {
            tc.position.add(-this.tileWorld.getWorldWidth() * Tile.TILE_SIZE, 0);
        }
        //***
        float x = tc.position.x;
        float y = tc.position.y;
        Camera camera = render.getCamera();
        x = Mathf.max(camera.viewportWidth / 2, x);
        y = Mathf.max(camera.viewportHeight / 2, y);
        x = Mathf.min(this.tileWorld.getWorldWidth() * Tile.TILE_SIZE - camera.viewportWidth / 2, x);
        y = Mathf.min(this.tileWorld.getWorldHeight() * Tile.TILE_SIZE - camera.viewportHeight / 2, y);
        camera.position.set(x, y, 0);
        this.render.applyViewport();
    }
}
