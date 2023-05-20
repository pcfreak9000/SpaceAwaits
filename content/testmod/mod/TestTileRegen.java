package mod;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.content.tiles.TileLooseRocks;
import de.pcfreak9000.spaceawaits.world.tile.IRegen;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class TestTileRegen implements IRegen<Tile> {
    
    public Tile create(Color maincolor) {
        Tile t = new TileLooseRocks();
        t.setColor(maincolor);
        return t;
    }
    
    @Override
    public Tile regenFrom(String recipe) {
        return null;
    }
    
}
