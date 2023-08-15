package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.ecs.ValidatingComponent;
import de.pcfreak9000.spaceawaits.world.World;

public class RandomTickComponent extends ValidatingComponent implements Component {
    
    public static interface RandomTickable {
        void tick(World world, Entity entity);
    }
    
    public double chance = 0.01;
    public RandomTickable tickable;
    
}
