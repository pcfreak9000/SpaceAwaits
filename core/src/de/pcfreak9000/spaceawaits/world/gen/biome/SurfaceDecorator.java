package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.MathUtil;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.gen.GenerationParameters;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.gen.ShapeSystem;
import de.pcfreak9000.spaceawaits.world.gen.feature.FeatureGenerator;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class SurfaceDecorator {
    
    private Array<FeatureGenerator> features = new Array<>();
    
    public void addFeature(FeatureGenerator fg) {
        this.features.add(fg);
    }
    
    public void decorate(TileSystem tiles, World world, GenerationParameters biomeGen, int tx, int length,
            RndHelper rnd) {
        //potentially shuffle features first?
        for (FeatureGenerator fg : features) {
            int count = MathUtil.randomRound(fg.getChancePerTile() * length, rnd.getRandom());
            for (int i = 0; i < count; i++) {
                int atx = rnd.getRandom().nextInt(length) + tx;
                int aty = biomeGen.getComponent(ShapeSystem.class).getHeight(atx);
                if (fg.getConditions() == null || fg.getConditions().canGenerate(tiles, world, biomeGen, atx, aty)) {
                    fg.getFeature().generate(world, tiles, atx, aty, rnd.getRandom());
                }
            }
        }
    }
}
