package de.pcfreak9000.spaceawaits.world.gen.biome;

import java.util.HashSet;
import java.util.Set;

import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.generation.IGen2D;
import de.pcfreak9000.spaceawaits.generation.RndHelper;
import de.pcfreak9000.spaceawaits.world.ITileArea;
import de.pcfreak9000.spaceawaits.world.WorldArea;
import de.pcfreak9000.spaceawaits.world.gen.CaveBiome;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public abstract class Biome implements IGen2D<Biome> {
    
    //At some point we probably need Biome variations
    
    protected ITileConfiguration tileConfig;
    
    protected SurfaceDecorator surfaceDeco;
    protected Decorator deco;
    
    protected CaveBiome caveBiome;
    
    public CaveBiome getCaveBiome() {
        return caveBiome;
    }
    
    public ITileConfiguration getTileConfig() {
        return tileConfig;
    }
    
    public SurfaceDecorator getSurfaceDeco() {
        return surfaceDeco;
    }
    
    public Decorator getDeco() {
        return deco;
    }
    
    private Set<Object> tags = new HashSet<>();
    
    public void addTag(Object tag) {
        this.tags.add(tag);
    }
    
    public boolean hasTag(Object tag) {
        return tags.contains(tag);
    }
    
    @Override
    public Biome generate(int tx, int ty) {
        return this;
    }
    
    //???????
    //gen Biome
    //  Caves 
    //decorate Biome
    //  Ores, Plants, etc
    //  Structures
    @Deprecated
    public abstract Tile genTileAt(int tx, int ty, TileLayer layer, GenerationParameters biomeGen, RndHelper rnd);
    
    @Deprecated
    public abstract void genStructureTiles(ITileArea tiles, GenerationParameters biomeGen, int tx, int ty,
            int structureDiv, RndHelper rnd);
    
    @Deprecated
    public abstract void populate(ITileArea tiles, WorldArea world, GenerationParameters biomeGen, int tx, int ty,
            int populateDiv, RndHelper rnd);
}
