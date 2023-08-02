package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.generation.RndHelper;
import de.pcfreak9000.spaceawaits.world.gen.CaveSystem;
import de.pcfreak9000.spaceawaits.world.gen.ShapeSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public interface ITileConfiguration {
    //add biome and/or biomegen parameter?
    //what about different tiles for front and back? two tileconfigs or add tilelayer as param as well?
    //force shape and caves as parameter or access it through generationparameters, i.e. is this class special to BiomeChunkGen which uses these classes anyways,
    //or should this be more general??
    Tile getTile(int x, int y, ShapeSystem shape, CaveSystem caves, GenerationParameters params, RndHelper rnd);
    
}
