package de.pcfreak9000.spaceawaits.world.gen;

import java.util.Random;

import com.badlogic.gdx.math.RandomXS128;

public class RndHelper {
    
    public static long getSeedAt(long seedMaster, int x) {
        seedMaster += 6793451682347862416L;
        seedMaster ^= x;
        return seedMaster;
    }
    
    public static long getSeedAt(long seedMaster, int x, int y) {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        long l = seedMaster;
        l += 6793451682347862416L;
        l *= result;
        //l ^= x;
       // l += 6793451682347862416L;
        //l ^= y;
        return l;
    }
    
    public static long getSeedAt(long seedMaster, double x, double y) {
        long l = seedMaster;
        l += 6793451682347862416L;
        l ^= Double.hashCode(x);
        l += 6793451682347862416L;
        l ^= Double.hashCode(y);
        return l;
    }
    
    private Random random;
    private long seedMaster;
    private long seed;
    
    public RndHelper() {
        this.random = new RandomXS128(seed);
    }
    
    public void set(long seedMaster, long seed) {
        this.seedMaster = seedMaster;
        this.seed = seed;
        this.random.setSeed(seed);
    }
    
    public Random getRandom() {
        return random;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public long getSeedMaster() {
        return seedMaster;
    }
    
}
