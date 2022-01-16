package de.pcfreak9000.spaceawaits.world.tile.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.spaceawaits.world.tile.BreakTileProgress;

public class BreakingTilesComponent implements Component {
    
    public final LongMap<BreakTileProgress> breaktiles;
    
    public BreakingTilesComponent(LongMap<BreakTileProgress> breaktiles) {
        this.breaktiles = breaktiles;
    }
    
}
