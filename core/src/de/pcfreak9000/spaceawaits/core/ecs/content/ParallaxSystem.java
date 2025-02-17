package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;

public class ParallaxSystem extends IteratingSystem {
    
    private SystemCache<CameraSystem> camsys = new SystemCache<>(CameraSystem.class);
    
    private float R0;
    
    public ParallaxSystem(float R0) {
        super(Family.all(ParallaxComponent.class, TransformComponent.class).get());
        this.R0 = R0;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent pc = Components.PARALLAX.get(entity);
        Vector2 pos = Components.TRANSFORM.get(entity).position;
        Vector3 camPos = camsys.get(getEngine()).getCamera().position;
        float factor = 1.0f - R0 / (R0 + pc.zdist);
        //this somehow works, but not perfectly. Is it even correct???? 
        float xadd = factor * camPos.x + (1.0f - factor) * pc.xEquiv;
        float yadd = factor * camPos.y + (1.0f - factor) * pc.yEquiv;
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
