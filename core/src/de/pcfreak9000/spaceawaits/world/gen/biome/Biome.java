package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public abstract class Biome {
    
    //???????
    //gen Biome
    //  Caves 
    //decorate Biome
    //  Ores, Plants, etc
    //  Structures
    
    public abstract void genTerrainTileAt(int tx, int ty, ITileArea tiles, BiomeGenCompBased biomeGen, RndHelper rnd);
    
    public abstract void populate(TileSystem tiles, World world, BiomeGenCompBased biomeGen, int tx, int ty,
            int populateDiv, RndHelper rnd);
}
