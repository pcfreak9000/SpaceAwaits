package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class BiomeChunkGenerator implements IChunkGenerator {
    
    private BiomeGenerator biomeGenerator;
    
    public BiomeChunkGenerator(BiomeGenerator biomeGenerator) {
        this.biomeGenerator = biomeGenerator;
    }
    
    @Override
    public void generateChunk(Chunk chunk, World world) {
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int x = i + chunk.getGlobalTileX();
                int y = j + chunk.getGlobalTileY();
                if (!world.getBounds().inBounds(x, y)) {
                    continue;
                }
                Biome biome = biomeGenerator.getBiome(x, y);
                biome.genTerrainTileAt(x, y, chunk, this.biomeGenerator);
            }
        }
    }
}
