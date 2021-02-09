package de.pcfreak9000.spaceawaits.world.light;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.pcfreak9000.spaceawaits.world.WorldAccessor;

public interface Light {
    
    void drawLight(SpriteBatch batch, WorldAccessor world);
}
