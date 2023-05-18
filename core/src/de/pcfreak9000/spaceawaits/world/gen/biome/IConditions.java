package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.gen.GenerationParameters;

public interface IConditions {
    boolean canGenerate(ITileArea tiles, World world, GenerationParameters params, int tx, int ty);
}
