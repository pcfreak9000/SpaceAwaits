package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Vector2;

public class UnitConversion {
    
    public static float[] texelToPhysicsspace(UnitConversion uc, float pixelPerTile, float height, float[] vert) {
        for (int i = 0; i < vert.length; i++) {
            if (i % 2 == 1) {
                vert[i] = height - vert[i];
            }
            vert[i] = uc.in(vert[i] / pixelPerTile);
        }
        return vert;
    }
    
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
