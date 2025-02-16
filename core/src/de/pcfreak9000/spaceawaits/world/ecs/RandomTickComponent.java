package de.pcfreak9000.spaceawaits.world.ecs;

import java.util.Random;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.ecs.ValidatingComponent;

public class RandomTickComponent extends ValidatingComponent implements Component {
    
    public static interface RandomTickable {
        void tick(Engine world, Entity entity, Random random);
    }
    
    public double chance = 0.01;
    public RandomTickable tickable;
    
}
