package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ChunkRenderComponent implements Component {
    
    public Chunk c;
    public boolean dirty;
    public int len;
    //    public int blen;
    public int cid = -1;
    public TileLayer layer;
}
