package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface IQueryCallback {
    
    /**
     * 
     * @param fix
     * @param conv
     * @return false to terminate the query
     */
    boolean reportFixture(Fixture fix, UnitConversion conv);
    
}
