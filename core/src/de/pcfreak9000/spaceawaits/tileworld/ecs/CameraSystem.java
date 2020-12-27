package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class CameraSystem extends IteratingSystem {
    
    private Camera camera;
    private TileWorld tileWorld;
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    public CameraSystem() {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.camera = svwe.worldMgr.getRenderInfo().getCamera();
        this.tileWorld = svwe.getTileWorldNew();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 positionState = this.transformMapper.get(entity).position;
        float x = positionState.x;
        float y = positionState.y;
        x = Mathf.max(this.camera.viewportWidth / 2, x);
        y = Mathf.max(this.camera.viewportHeight / 2, y);
        x = Mathf.min(this.tileWorld.getWorldWidth() * Tile.TILE_SIZE - this.camera.viewportWidth / 2, x);
        y = Mathf.min(this.tileWorld.getWorldHeight() * Tile.TILE_SIZE - this.camera.viewportHeight / 2, y);
        this.camera.position.set(x, y, 0);
        //temporary wrap around
        TransformComponent tc = this.transformMapper.get(entity);
        if (tc.position.x < 0) {
            tc.position.add(this.tileWorld.getWorldWidth() * Tile.TILE_SIZE, 0);
        } else if (tc.position.x > this.tileWorld.getWorldWidth() * Tile.TILE_SIZE) {
            tc.position.add(-this.tileWorld.getWorldWidth() * Tile.TILE_SIZE, 0);
        }
    }
}
