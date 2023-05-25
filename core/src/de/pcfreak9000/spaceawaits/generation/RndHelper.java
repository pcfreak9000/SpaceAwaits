package de.pcfreak9000.spaceawaits.generation;

import java.util.Random;

import com.badlogic.gdx.math.RandomXS128;

import de.omnikryptec.math.Mathd;

public class RndHelper {
    
    private static Random randomStatic = new RandomXS128();
    
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
        l ^= Double.hashCode(x) * 73856093L;
        l ^= Double.hashCode(y) * 83492791L;
        //        l += 6793451682347862416L;
        //        l ^= Double.hashCode(x);
        //        l += 6793451682347862416L;
        //        l ^= Double.hashCode(y);
        return l;
    }
    
    public static float randomAt(double x, long seed) {
        int xint = Mathd.floori(x);
        
        int samplex = xint;
        double curDist = Double.POSITIVE_INFINITY;
        for (int i = -3; i <= 3; i++) {
            randomStatic.setSeed(RndHelper.getSeedAt(seed + 1, xint + i));
            double xpos = xint + i + randomStatic.nextDouble() * 2.0 - 1.0;
            double dist = Mathd.abs(xpos - x);
            if (dist < curDist) {
                curDist = dist;
                samplex = Mathd.floori(xpos);
            }
        }
        randomStatic.setSeed(RndHelper.getSeedAt(seed, samplex));
        return (float) randomStatic.nextDouble();
        
    }
    
    public static float randomAt(double x, double y, long seed) {
        int xint = Mathd.floori(x);
        int yint = Mathd.floori(y);
        
        int samplex = xint;
        int sampley = yint;
        double curDist2 = Double.POSITIVE_INFINITY;
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                randomStatic.setSeed(RndHelper.getSeedAt(seed + 2, xint + i, yint + j));
                double xpos = xint + i + randomStatic.nextDouble() * 2.0 - 1;
                randomStatic.setSeed(RndHelper.getSeedAt(seed + 1, xint + i, yint + j));
                double ypos = yint + j + randomStatic.nextDouble() * 2.0 - 1;
                double dist2 = Mathd.square(xpos - x) + Mathd.square(ypos - y);
                if (dist2 < curDist2) {
                    curDist2 = dist2;
                    samplex = Mathd.floori(xpos);
                    sampley = Mathd.floori(ypos);
                }
            }
        }
        randomStatic.setSeed(RndHelper.getSeedAt(seed, samplex, sampley));
        return (float) randomStatic.nextDouble();
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
