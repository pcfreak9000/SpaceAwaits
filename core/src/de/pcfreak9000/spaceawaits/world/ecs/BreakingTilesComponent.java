package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.spaceawaits.world.BreakTile;

public class BreakingTilesComponent implements Component {
    
    public final LongMap<BreakTile> breaktiles;
    
    public BreakingTilesComponent(LongMap<BreakTile> breaktiles) {
        this.breaktiles = breaktiles;
    }
}
