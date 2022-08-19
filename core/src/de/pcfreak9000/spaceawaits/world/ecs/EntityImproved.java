package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;

public class EntityImproved extends Entity {
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Entity ");
        b.append("flags=").append(flags).append(' ');
        b.append(this.getComponents().toString());
        return b.toString();
    }
    
}
