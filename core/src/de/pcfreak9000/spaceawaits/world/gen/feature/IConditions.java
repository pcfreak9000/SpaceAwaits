package de.pcfreak9000.spaceawaits.world.gen.feature;

import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.world.ITileArea;
import de.pcfreak9000.spaceawaits.world.WorldArea;

public interface IConditions {
    boolean canGenerate(ITileArea tiles, WorldArea world, GenerationParameters params, int tx, int ty);
}
