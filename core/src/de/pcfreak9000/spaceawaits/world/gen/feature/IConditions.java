package de.pcfreak9000.spaceawaits.world.gen.feature;

import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;

public interface IConditions {
    boolean canGenerate(ITileArea tiles, World world, GenerationParameters params, int tx, int ty);
}
