package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.render.WorldScreen;

public class ParallaxSystem extends IteratingSystem {
    
    private static final float R0 = WorldScreen.VISIBLE_TILES_MIN;
    
    private Camera camera;
    
    public ParallaxSystem(GameScreen renderer) {
        super(Family.all(ParallaxComponent.class, TransformComponent.class).get());
        this.camera = renderer.getCamera();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent pc = Components.PARALLAX.get(entity);
        Vector3 positionState = camera.position;
        float factor = 1.0f - R0 / (R0 + pc.zdist);
        float xadd = factor * positionState.x + pc.xOffset;
        float yadd = factor * positionState.y + pc.yOffset;
        Vector2 pos = Components.TRANSFORM.get(entity).position;
        //This is some wild fuckery. But at least this way this stuff stays entirely seperated from the transform (except for the above line of course)
        //It allows moving stuff via the transform even though the stuff is parallaxed.
        pos.x -= pc.prevxadd;
        pos.y -= pc.prevyadd;
        pc.prevxadd = xadd;
        pc.prevyadd = yadd;
        pos.x += xadd;
        pos.y += yadd;
    }
    
}
