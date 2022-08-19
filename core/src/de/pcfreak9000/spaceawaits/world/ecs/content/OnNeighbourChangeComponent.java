package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class OnNeighbourChangeComponent implements Component {
    
    public static interface OnNeighbourTileChange {
        void onNeighbourTileChange(World world, TileSystem tileSystem, Entity entity, Tile newNeighbour,
                Tile oldNeighbour, int ngtx, int ngty, TileLayer layer);
    }
    
    public OnNeighbourTileChange onNeighbourTileChange;
    
}
