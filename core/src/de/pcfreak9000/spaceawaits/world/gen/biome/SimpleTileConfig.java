package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.generation.RndHelper;
import de.pcfreak9000.spaceawaits.world.gen.CaveSystem;
import de.pcfreak9000.spaceawaits.world.gen.ShapeSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class SimpleTileConfig implements ITileConfiguration {
    
    private Tile top;
    private Tile mid;
    private Tile bot;
    private int midThickness;
    
    public SimpleTileConfig(Tile top, Tile mid, Tile bot, int midThickness) {
        this.top = top;
        this.mid = mid;
        this.bot = bot;
        this.midThickness = midThickness;
    }
    
    public SimpleTileConfig(Tile top, Tile mid, Tile bot) {
        this(top, mid, bot, 3);
    }
    
    //We don't need getNSD with this, we could just return the tile
    //We should probably rename this class to LayerTileConfig and make SimpleTileConfig the one-tile-type case, but for now this should be okay
    public SimpleTileConfig(Tile tile) {
        this(tile, tile, tile);
    }
    
    @Override
    public Tile getTile(int x, int y, ShapeSystem shape, CaveSystem caves, GenerationParameters params, RndHelper rnd) {
        //oh boi, the surface stuff is much more complicated than it seems
        //doesn't account for the "surface" (i.e. the border between two biomes) of a biome below ground.
        float nsd = shape.getNextSurfaceDistance(x, y);
        if (nsd == 0) {//Hmmm, float comparison
            return top;
        } else if (nsd <= midThickness) {
            return mid;
        }
        return bot;
    }
    
}
