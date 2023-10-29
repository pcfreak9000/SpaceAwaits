package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.world.WorldArea;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class OreGenTileFeature implements IFeature {
    
    private Tile ore;
    private Tile replace;
    private int min;
    private int max;
    
    public OreGenTileFeature(Tile ore, int min, int max) {
        this(ore, min, max, null);
    }
    
    public OreGenTileFeature(Tile ore, int min, int max, Tile replace) {
        this.ore = ore;
        this.min = min;
        this.max = max;
        this.replace = replace;
    }
    
    @Override
    public boolean generate(WorldArea world, ITileArea tiles, int tx, int ty, Random rand) {
        int count = min + rand.nextInt(max - min);
        int maxloop = 3 * count;
        while (count > 0 && maxloop > 0) {
            maxloop--;
            Tile front = tiles.getTile(tx, ty, TileLayer.Front);
            if (front.canBeReplacedByOre(ore) || front == replace) {
                tiles.setTile(tx, ty, TileLayer.Front, ore);
                count--;
            }
            for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                Tile frontfor = tiles.getTile(tx + d.dx, ty + d.dy, TileLayer.Front);
                if (count > 0 && (frontfor.canBeReplacedByOre(ore) || frontfor == replace)) {
                    tiles.setTile(tx + d.dx, ty + d.dy, TileLayer.Front, ore);
                    count--;
                }
            }
            if (count > 0) {
                Direction d = Direction.MOORE_NEIGHBOURS[rand.nextInt(Direction.MOORE_NEIGHBOURS.length)];
                tx += d.dx;
                ty += d.dy;
            }
        }
        return true;
    }
    
}
