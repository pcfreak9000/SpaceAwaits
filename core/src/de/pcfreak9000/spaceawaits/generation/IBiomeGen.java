package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;

public interface IBiomeGen {
    Biome getFrom(int tx, int ty);
}
