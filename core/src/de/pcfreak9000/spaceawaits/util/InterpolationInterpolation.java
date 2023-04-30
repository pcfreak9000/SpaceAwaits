package de.pcfreak9000.spaceawaits.util;

import com.badlogic.gdx.math.Interpolation;

public class InterpolationInterpolation extends Interpolation {
    
    public static float apply(float start, float end, float a, Interpolation starti, Interpolation endi,
            Interpolation interpolation) {
        if (starti == endi) {
            return starti.apply(start, end, a);
        }
        return interpolation.apply(starti.apply(start, end, a), endi.apply(start, end, a), a);
    }
    
    private Interpolation start;
    private Interpolation end;
    private Interpolation value;
    
    public InterpolationInterpolation(Interpolation start, Interpolation end, Interpolation value) {
        this.start = start;
        this.end = end;
        this.value = value;
    }
    
    @Override
    public float apply(float a) {
        return value.apply(start.apply(a), end.apply(a), a);
    }
    
}