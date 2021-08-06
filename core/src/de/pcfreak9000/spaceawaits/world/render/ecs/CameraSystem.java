package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

public class CameraSystem extends IteratingSystem {
    private static final boolean DEBUG = false;
    
    private static final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private static final ComponentMapper<PlayerInputComponent> playerMapper = ComponentMapper
            .getFor(PlayerInputComponent.class);
    private World world;
    
    public CameraSystem(World world) {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        this.world = world;
    }
    
    //Dedicated camera component at some time?
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = transformMapper.get(entity);
        PlayerInputComponent pc = playerMapper.get(entity);
        float x = tc.position.x + pc.offx;
        float y = tc.position.y + pc.offy;
        Camera camera = SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().getView().getCamera();
        if (!DEBUG) {
            x = Mathf.max(camera.viewportWidth / 2, x);
            y = Mathf.max(camera.viewportHeight / 2, y);
            x = Mathf.min(world.getBounds().getWidth() - camera.viewportWidth / 2, x);
            y = Mathf.min(world.getBounds().getHeight() - camera.viewportHeight / 2, y);
        }
        camera.position.set(x, y, 0);
        SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().applyViewport();
    }
}
