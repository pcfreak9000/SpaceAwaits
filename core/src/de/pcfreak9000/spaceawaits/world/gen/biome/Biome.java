package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.badlogic.gdx.utils.ObjectMap;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public abstract class Biome {
    
    protected final ObjectMap<Class<? extends BiomeInterpolatable>, BiomeInterpolatable> interpolators = new ObjectMap<>();
    
    public boolean hasInterpolatable(Class<? extends BiomeInterpolatable> clazz) {
        return interpolators.containsKey(clazz);
    }
    
    public BiomeInterpolatable getInterpolatable(Class<? extends BiomeInterpolatable> clazz) {
        return interpolators.get(clazz);
    }
    
    //???????
    //gen Biome
    //  Caves 
    //decorate Biome
    //  Ores, Plants, etc
    //  Structures
    
    public abstract void genTerrainTileAt(int tx, int ty, Chunk chunk, BiomeGenerator biomeGen);
    
    //args: chunk? area?
    public abstract void decorate(Chunk chunk);
}
