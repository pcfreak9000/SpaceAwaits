package de.pcfreak9000.spaceawaits.serialize;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.core.ecs.EntityFactory;

public class SerializeEntityComponent implements Component {
    
    public final EntityFactory factory;
    
    public SerializeEntityComponent(EntityFactory fac) {
        this.factory = fac;
    }
    
}
