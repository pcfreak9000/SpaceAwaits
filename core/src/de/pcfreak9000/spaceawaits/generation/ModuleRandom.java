package de.pcfreak9000.spaceawaits.generation;

import java.util.Random;

import org.apache.commons.lang3.NotImplementedException;

import com.badlogic.gdx.math.RandomXS128;
import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.SeededModule;

public class ModuleRandom extends SeededModule {
    
    private Random random;
    
    public ModuleRandom() {
        this.random = new RandomXS128();
    }
    
    @Override
    public double get(double x, double y) {
        this.random.setSeed(RndHelper.getSeedAt(getSeed(), x, y));
        return this.random.nextDouble(-1, 1);
    }
    
    @Override
    public double get(double x, double y, double z) {
        throw new NotImplementedException("3D");
    }
    
    @Override
    public double get(double x, double y, double z, double w) {
        throw new NotImplementedException("4D");
    }
    
    @Override
    public double get(double x, double y, double z, double w, double u, double v) {
        throw new NotImplementedException("6D");
    }
    
    @Override
    public Module buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap map) {
        throw new NotImplementedException("buildFromPropertyMap");
    }
    
    @Override
    public void writeToMap(ModuleMap moduleMap) {
        ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
        this.writeSeed(modulePropertyMap);
        moduleMap.put(this.getId(), modulePropertyMap);
    }
    
}
