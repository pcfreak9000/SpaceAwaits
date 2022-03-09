package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.LongArray;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ChunkRenderComponent implements Component {
    
    public Chunk chunk;
    public LongArray tilePositions;
    public TileLayer layer;
}
