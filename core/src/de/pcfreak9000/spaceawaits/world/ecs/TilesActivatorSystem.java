package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.core.ecs.content.ActivatorSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;

public class TilesActivatorSystem extends ActivatorSystem {
    private final SystemCache<PhysicsSystem> phys = new SystemCache<>(PhysicsSystem.class);
    
    protected Array<Object> getEntities(float mousex, float mousey) {
        return phys.get(getEngine()).queryXY(mousex, mousey,
                (udh, uc) -> udh.isEntity() && Components.ACTIVATOR.has(udh.getEntity()));
    }
}
