package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public class BreakingComponent implements Component {
    
    public float addProgress;
    public IBreaker breaker;
    
    float progress;
    
    public float getProgress() {
        return progress;
    }
}
