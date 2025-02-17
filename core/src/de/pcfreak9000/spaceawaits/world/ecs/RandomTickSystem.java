package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.core.ecs.content.RandomSystem;

public class RandomTickSystem extends IntervalIteratingSystem {
    
    private SystemCache<RandomSystem> randsys = new SystemCache<>(RandomSystem.class);
    
    public RandomTickSystem() {
        super(Family.all(RandomTickComponent.class).get(), 0.2f);
    }
    
    @Override
    protected void processEntity(Entity entity) {
        RandomTickComponent rtc = Components.RANDOM_TICK.get(entity);
        if (randsys.get(getEngine()).getRandom().nextDouble() < rtc.chance) {
            if (rtc.validate(entity)) {
                Components.RANDOM_TICK.get(entity).tickable.tick(getEngine(), entity,
                        randsys.get(getEngine()).getRandom());
            }
        }
    }
    
}
