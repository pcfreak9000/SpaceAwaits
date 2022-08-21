package de.pcfreak9000.spaceawaits.world.ecs.content;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import de.pcfreak9000.spaceawaits.world.World;

public class RandomTickSystem extends IntervalIteratingSystem {
    
    private Random tickRandom;
    private World world;
    
    public RandomTickSystem(Random random, World world) {
        super(Family.all(RandomTickComponent.class).get(), 0.2f);
        this.tickRandom = random;
        this.world = world;
    }
    
    @Override
    protected void processEntity(Entity entity) {
        if (tickRandom.nextDouble() < 0.01) {
            RandomTickComponent rtc = Components.RANDOM_TICK.get(entity);
            if (rtc.validate(entity)) {
                Components.RANDOM_TICK.get(entity).tickable.tick(world);
            }
        }
    }
    
}
