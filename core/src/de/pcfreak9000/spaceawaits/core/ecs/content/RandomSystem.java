package de.pcfreak9000.spaceawaits.core.ecs.content;

import java.util.Random;

import com.badlogic.ashley.core.EntitySystem;

public class RandomSystem extends EntitySystem {
    
    private Random random;
    
    public RandomSystem(Random random) {
        setProcessing(false);
        this.random = random;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
}
