package de.pcfreak9000.spaceawaits.world.gen;

import com.badlogic.ashley.core.Engine;

public interface IWorldGenerator {
    
    void generate(Engine world);
    
    default void onLoading(Engine world) {
    }
}
