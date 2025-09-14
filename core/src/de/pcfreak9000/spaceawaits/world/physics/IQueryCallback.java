package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.box2d.structs.b2ShapeId;

public interface IQueryCallback {
    
    /**
     * 
     * @param fix
     * @param conv
     * @return false to terminate the query
     */
    boolean reportFixture(b2ShapeId fix, UnitConversion conv);
    
}
