package de.pcfreak9000.spaceawaits.util;

import com.badlogic.gdx.math.Interpolation;

public interface IStepWiseComponent {
    Interpolation getInterpolation();
    
    int getInterpolationDistance();

}
