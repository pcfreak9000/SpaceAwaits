package de.pcfreak9000.spaceawaits.world.gen.feature;

import de.pcfreak9000.spaceawaits.world.gen.biome.IConditions;

public class FeatureGenerator {
    
    private float chancePerTile;
    private IFeature feature;
    private IConditions conditions;
    
    public FeatureGenerator(float chancePerTile, IFeature feature, IConditions cond) {
        this.chancePerTile = chancePerTile;
        this.feature = feature;
        this.conditions = cond;
    }
    
    public float getChancePerTile() {
        return chancePerTile;
    }
    
    public IFeature getFeature() {
        return feature;
    }
    
    public IConditions getConditions() {
        return conditions;
    }
    
    //    public void generate(int tilecount, ITileArea tiles, World world, int tx, int ty, Random rand) {
    //        int count = MathUtil.randomRound(tilecount * chancePerTile, rand);
    //        for (int i = 0; i < count; i++) {
    //            int x = rand.nextInt(area) + tx;
    //            int y = rand.nextInt(area) + ty;
    //            int height = biomeGen.getComponent(ShapeSystem.class).getHeight(x);
    //            if (height - y > 5) {
    //                feature.generate(tiles, x, y, rand);
    //            }
    //        }
    //    }
    
}
