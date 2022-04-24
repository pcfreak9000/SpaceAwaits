package de.pcfreak9000.spaceawaits.world.gen.biome;

import java.util.Random;

import com.badlogic.gdx.utils.ObjectMap;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public abstract class Biome {
    
    public static final int POPULATE_DIV = 16;
    
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
    
    public abstract void genTerrainTileAt(int tx, int ty, ITileArea tiles, BiomeGenerator biomeGen, Random rand);
    
    public abstract void populate(TileSystem tiles, World world, BiomeGenerator biomeGen, int tx, int ty, Random rand);
}
