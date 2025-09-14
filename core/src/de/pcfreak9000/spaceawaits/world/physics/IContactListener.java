package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.box2d.structs.b2Manifold;

public interface IContactListener {
    
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, b2Manifold manifold, UnitConversion conv,
            Engine world);
    
    public boolean endContact(UserDataHelper owner, UserDataHelper other, UnitConversion conv,
            Engine world);
}
