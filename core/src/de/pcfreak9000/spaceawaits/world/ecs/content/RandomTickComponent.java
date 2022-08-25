package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.ValidatingComponent;

public class RandomTickComponent extends ValidatingComponent implements Component {
    
    public static interface RandomTickable {
        void tick(World world, Entity entity);
    }
    
    public RandomTickable tickable;
    
}
