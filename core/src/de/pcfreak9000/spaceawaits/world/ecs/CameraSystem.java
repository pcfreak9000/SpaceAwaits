package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldRenderInfo;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class CameraSystem extends IteratingSystem {
    
    private WorldRenderInfo render;
    private WorldAccessor world;
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    public CameraSystem() {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.render = svwe.worldMgr.getRenderInfo();
        this.world = svwe.worldMgr.getWorldAccess();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = this.transformMapper.get(entity);
        //temporary wrap around
        if (tc.position.x < 0) {
            tc.position.add(world.getMeta().getWidth() * Tile.TILE_SIZE, 0);
        } else if (tc.position.x > world.getMeta().getWidth() * Tile.TILE_SIZE) {
            tc.position.add(-world.getMeta().getWidth() * Tile.TILE_SIZE, 0);
        }
        //***
        float x = tc.position.x;
        float y = tc.position.y;
        Camera camera = render.getCamera();
        x = Mathf.max(camera.viewportWidth / 2, x);
        y = Mathf.max(camera.viewportHeight / 2, y);
        x = Mathf.min(world.getMeta().getWidth() * Tile.TILE_SIZE - camera.viewportWidth / 2, x);
        y = Mathf.min(world.getMeta().getHeight() * Tile.TILE_SIZE - camera.viewportHeight / 2, y);
        camera.position.set(x, y, 0);
        this.render.applyViewport();
    }
}
