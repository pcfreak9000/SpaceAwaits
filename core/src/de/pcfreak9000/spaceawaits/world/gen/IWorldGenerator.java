package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.World;

public interface IWorldGenerator {
    
    void generate(World world);
    
    default void onLoading(World world) {
    }
}
