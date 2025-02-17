package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;

public class FollowMouseSystem extends IteratingSystem {
    
    private SystemCache<CameraSystem> camsys = new SystemCache<>(CameraSystem.class);
    
    public FollowMouseSystem() {
        super(Family.all(FollowMouseComponent.class, TransformComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FollowMouseComponent fmc = Components.FOLLOW_MOUSE.get(entity);
        TransformComponent tc = Components.TRANSFORM.get(entity);
        Vector2 pos = camsys.get(getEngine()).getMouseWorldPos();
        float x = pos.x;
        float y = pos.y;
        if (fmc.tiled) {
            x = Mathf.floor(x);
            y = Mathf.floor(y);
        }
        tc.position.set(x + fmc.xoffset, y + fmc.yoffset);
    }
    
}
