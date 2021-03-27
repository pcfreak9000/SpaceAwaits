package de.pcfreak9000.spaceawaits.serialize;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;

public class SerializeEntityComponent implements Component {
    
    public final WorldEntityFactory factory;
    
    public SerializeEntityComponent(WorldEntityFactory fac) {
        this.factory = fac;
    }
    
}
