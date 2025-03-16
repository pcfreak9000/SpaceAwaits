package de.pcfreak9000.spaceawaits.world.render;

public interface EffectRenderer {
    void enterEffect();

    void exitAndRenderEffect();

    float getBeginLayer();

    float getEndLayer();
}
