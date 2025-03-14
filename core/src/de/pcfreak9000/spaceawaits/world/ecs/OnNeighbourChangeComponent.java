package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.ecs.ValidatingComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class OnNeighbourChangeComponent extends ValidatingComponent {
    
    public static interface OnNeighbourTileChange {
        void onNeighbourTileChange(Engine world, TileSystem tileSystem, Entity entity, Tile newNeighbour,
                Tile oldNeighbour, int ngtx, int ngty, TileLayer layer);
    }
    
    public OnNeighbourTileChange onNeighbourTileChange;
    
}
