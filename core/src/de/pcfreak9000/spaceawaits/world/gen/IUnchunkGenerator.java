package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;
import de.pcfreak9000.spaceawaits.world.World;

@Deprecated
public interface IUnchunkGenerator {
    
    void generateUnchunk(SerializableEntityList entities, World world);
    
    default void regenerateUnchunk(SerializableEntityList entities, World world) {
    }
    
}
