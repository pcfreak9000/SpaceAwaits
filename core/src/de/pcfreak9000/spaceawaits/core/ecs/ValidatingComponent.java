package de.pcfreak9000.spaceawaits.core.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

//Hmmmmmm......
/**
 * For components which contain functionality accessing other components. This
 * can be used to make sure the entity has the components required by that
 * functionality.
 */
public abstract class ValidatingComponent implements Component {
    private ComponentMapper<?>[] mappers;
    
    public void setRequired(ComponentMapper<?>... mappers) {
        this.mappers = mappers;
    }
    
    public boolean validate(Entity e) {
        if (mappers == null || mappers.length == 0) {
            return true;
        }
        for (ComponentMapper<?> map : mappers) {
            if (!map.has(e)) {
                return false;
            }
        }
        return true;
    }
}
