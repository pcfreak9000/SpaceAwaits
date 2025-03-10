package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.systems.IntervalSystem;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class AutosaveSystem extends IntervalSystem {

    public AutosaveSystem(float interval) {
        super(interval);
    }

    @Override
    protected void updateInterval() {
        SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().saveGame();
    }
}
