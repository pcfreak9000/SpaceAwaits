package de.pcfreak9000.spaceawaits.world.ecs;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

public class RandomTickSystem extends IntervalIteratingSystem {
    
    private Random tickRandom;
    
    public RandomTickSystem(Random random) {
        super(Family.all(RandomTickComponent.class).get(), 0.2f);
        this.tickRandom = random;
    }
    
    @Override
    protected void processEntity(Entity entity) {
        RandomTickComponent rtc = Components.RANDOM_TICK.get(entity);
        if (tickRandom.nextDouble() < rtc.chance) {
            if (rtc.validate(entity)) {
                Components.RANDOM_TICK.get(entity).tickable.tick(getEngine(), entity, this.tickRandom);
            }
        }
    }
    
}
