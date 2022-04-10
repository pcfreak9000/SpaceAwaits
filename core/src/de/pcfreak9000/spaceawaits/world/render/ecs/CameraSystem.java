package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;

public class CameraSystem extends IteratingSystem {
    
    private World world;
    
    public CameraSystem(World world) {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        this.world = world;
    }
    
    //Dedicated camera component at some time?
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = Components.TRANSFORM.get(entity);
        PlayerInputComponent pc = Components.PLAYER_INPUT.get(entity);
        float x = tc.position.x + pc.offx;
        float y = tc.position.y + pc.offy;
        Camera camera = SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().getCurrentView().getCamera();
        if (!SpaceAwaits.DEBUG_CAMERA) {
            x = Mathf.max(camera.viewportWidth / 2, x);
            y = Mathf.max(camera.viewportHeight / 2, y);
            x = Mathf.min(world.getBounds().getWidth() - camera.viewportWidth / 2, x);
            y = Mathf.min(world.getBounds().getHeight() - camera.viewportHeight / 2, y);
        }
        camera.position.set(x, y, 0);
        SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().applyViewport();
    }
}
