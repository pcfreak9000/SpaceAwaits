package de.pcfreak9000.spaceawaits.world.gen.biome;

import java.util.HashSet;
import java.util.Set;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public abstract class Biome {
    
    private Set<Object> tags = new HashSet<>();
    
    public void addTag(Object tag) {
        this.tags.add(tag);
    }
    
    public boolean hasTag(Object tag) {
        return tags.contains(tag);
    }
    
    //???????
    //gen Biome
    //  Caves 
    //decorate Biome
    //  Ores, Plants, etc
    //  Structures
    
    public abstract Tile genTileAt(int tx, int ty, TileLayer layer, BiomeSystem biomeGen, RndHelper rnd);
    
    public abstract void genStructureTiles(TileSystem tiles, BiomeSystem biomeGen, int tx, int ty, int structureDiv,
            RndHelper rnd);
    
    public abstract void populate(TileSystem tiles, World world, BiomeSystem biomeGen, int tx, int ty, int populateDiv,
            RndHelper rnd);
}
