package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.graphics.Color;

public class Light {
    private float colorPacked;
    private float intensity;
    
    public void setColor(Color color) {
        this.colorPacked = color.toFloatBits();
    }
    
    public float getColorPacked() {
        return colorPacked;
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    public float getIntensity() {
        return intensity;
    }
}
