package layerteststuff;

import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeInterpolatable;

public class HeightInterpolatable implements BiomeInterpolatable {
    
    private HeightSupplier hs;
    
    public HeightInterpolatable(HeightSupplier hs) {
        this.hs = hs;
    }
    
    @Override
    public float get(int tx, int ty) {
        return hs.get(tx, ty);
    }
    
}
