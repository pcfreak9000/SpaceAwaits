package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.math.Interpolation;

import de.pcfreak9000.spaceawaits.util.IStepWiseComponent;

public class TestStepwiseComponent implements IStepWiseComponent {
    
    private Interpolation interpol;
    private int interpolconst;
    
    public TestStepwiseComponent(Interpolation interpol, int interpolconst) {
        this.interpol = interpol;
        this.interpolconst = interpolconst;
    }
    
    @Override
    public Interpolation getInterpolation() {
        return interpol;
    }
    
    @Override
    public int getInterpolationDistance() {
        return interpolconst;
    }
    
}
