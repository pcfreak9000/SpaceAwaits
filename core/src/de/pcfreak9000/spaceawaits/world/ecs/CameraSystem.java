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

public class CameraSystem extends IteratingSystem {
    private static final boolean DEBUG = false;
    private WorldAccessor world;
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<PlayerInputComponent> playerMapper = ComponentMapper
            .getFor(PlayerInputComponent.class);
    
    public CameraSystem() {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.world = svwe.worldMgr.getWorldAccess();
    }
    
    //Dedicated camera component at some time?
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = this.transformMapper.get(entity);
        PlayerInputComponent pc = this.playerMapper.get(entity);
        float x = tc.position.x + pc.offx;
        float y = tc.position.y + pc.offy;
        Camera camera = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getCamera();
        if (!DEBUG) {
            x = Mathf.max(camera.viewportWidth / 2, x);
            y = Mathf.max(camera.viewportHeight / 2, y);
            x = Mathf.min(world.getWorldBounds().getWidth() - camera.viewportWidth / 2, x);
            y = Mathf.min(world.getWorldBounds().getHeight() - camera.viewportHeight / 2, y);
        }
        camera.position.set(x, y, 0);
        SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().applyViewport();
    }
}
