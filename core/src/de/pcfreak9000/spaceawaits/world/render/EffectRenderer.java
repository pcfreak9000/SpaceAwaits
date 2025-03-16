package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.Engine;

public interface EffectRenderer {
    void enterEffect();

    void exitAndRenderEffect();

    float getBeginLayer();

    float getEndLayer();

    default void addedToEngineInternal(Engine engine) {
    }

    default void removedFromEngineInternal(Engine engine) {
    }
}
