package de.pcfreak9000.spaceawaits.world.ecs;

import java.util.Iterator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.world.BreakTile;

public class BreakingTileSystem extends IteratingSystem {
    private static final ComponentMapper<BreakingTilesComponent> MAPPER = ComponentMapper
            .getFor(BreakingTilesComponent.class);
    
    public BreakingTileSystem() {
        super(Family.all(BreakingTilesComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Iterator<BreakTile> it = MAPPER.get(entity).breaktiles.values().iterator();
        while (it.hasNext()) {
            BreakTile t = it.next();
            if (t.getLast() == t.getProgress()) {
                it.remove();
            } else {
                t.setLast(t.getProgress());
            }
        }
    }
    
}
