package de.pcfreak9000.spaceawaits.world.gen;

import java.util.function.LongFunction;

import com.badlogic.gdx.math.Interpolation;

import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.IStepWiseComponent;

public class HeightBiome implements IStepWiseComponent {
    
    protected Interpolation interpolation = Interpolation.linear;
    protected int interpolationDistance = 10;
    
    protected float defaultAmplitude;
    protected float defaultOffset;
    
    //heightgen has (planetary) scale
    
    //heightgen which doesnt want a plane just doesnt put it into its selection structures. this replaces the interval of applicability
    //what if a biome is put which doesnt like the smaller scale, ie is too big? -> someone fucked up his selection structures then
    
    //getAmplitude(planetaryScale) + seed etc?
    public float getAmplitude(float maxamplitude) {
        return defaultAmplitude;
    }
    
    //getOffset?? + seed etc?
    public float getOffset(float maxamplitude, float amplitude) {
        return defaultOffset;
    }
    
    //spline transform -> no, this number manipulation stuff and doesn't belong into the height biome. maybe its own Module?
    //Module supplier for noisegen or default noise?? this isnt so nice?
    protected LongFunction<NoiseGenerator> noiseGenCreator;
    
    public LongFunction<NoiseGenerator> getNoiseGenProvider() {
        return noiseGenCreator;
    }
    
    @Override
    public Interpolation getInterpolation() {
        return interpolation;
    }
    
    @Override
    public int getInterpolationDistance() {
        return interpolationDistance;
    }
}
