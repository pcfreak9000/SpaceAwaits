package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class FollowMouseSystem extends IteratingSystem {
    private static final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private static final ComponentMapper<FollowMouseComponent> followMapper = ComponentMapper
            .getFor(FollowMouseComponent.class);
    
    private GameRenderer renderer;
    
    public FollowMouseSystem(GameRenderer renderer) {
        super(Family.all(FollowMouseComponent.class).get());
        this.renderer = renderer;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FollowMouseComponent fmc = followMapper.get(entity);
        TransformComponent tc = transformMapper.get(entity);
        Vector2 pos = renderer.getMouseWorldPos();
        float x = pos.x;
        float y = pos.y;
        if (fmc.tiled) {
            x = Mathf.floor(x);
            y = Mathf.floor(y);
        }
        tc.position.set(x + fmc.xoffset, y + fmc.yoffset);
    }
    
}
