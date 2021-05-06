package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.physics.box2d.Fixture;

import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;

public interface IQueryCallback {
    
    /**
     * 
     * @param fix
     * @param conv
     * @return false to terminate the query
     */
    boolean reportFixture(Fixture fix, UnitConversion conv);
    
}
