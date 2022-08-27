package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;

public class BreakingComponent implements Component {
    
    public float addProgress;
    public ITileBreaker breaker;
    
    float progress;
    
    public float getProgress() {
        return progress;
    }
}
