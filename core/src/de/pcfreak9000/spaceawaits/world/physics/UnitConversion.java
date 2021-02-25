package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Vector2;

public class UnitConversion {
    private final float conversionfactor;
    
    public UnitConversion(float factor) {
        this.conversionfactor = factor;
    }
    
    public float getConversionFactor() {
        return this.conversionfactor;
    }
    
    public float out(float in) {
        return in * this.conversionfactor;
    }
    
    public float in(float in) {
        return in / this.conversionfactor;
    }
    
    public Vector2 out(Vector2 in) {
        return in.scl(conversionfactor);
    }
    
    public Vector2 in(Vector2 in) {
        return in.scl(1 / conversionfactor);
    }
}
