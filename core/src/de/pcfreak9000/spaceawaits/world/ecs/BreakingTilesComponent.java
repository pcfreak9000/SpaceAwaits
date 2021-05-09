package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.spaceawaits.world.BreakTileProgress;

public class BreakingTilesComponent implements Component {
    
    public final LongMap<BreakTileProgress> breaktiles;
    
    public BreakingTilesComponent(LongMap<BreakTileProgress> breaktiles) {
        this.breaktiles = breaktiles;
    }
}
