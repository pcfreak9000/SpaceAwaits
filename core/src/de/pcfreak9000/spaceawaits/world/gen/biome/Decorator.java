package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.MathUtil;
import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.generation.RndHelper;
import de.pcfreak9000.spaceawaits.world.WorldArea;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.gen.feature.FeatureGenData;

public class Decorator {
    private Array<FeatureGenData> features = new Array<>();
    
    public void addFeature(FeatureGenData fg) {
        this.features.add(fg);
    }
    
    public void decorate(ITileArea tiles, WorldArea world, GenerationParameters biomeGen, int tx, int ty, int length,
            RndHelper rnd) {
        //potentially shuffle features first?
        for (FeatureGenData fg : features) {
            int count = MathUtil.randomRound(fg.getChancePerTile() * length * length, rnd.getRandom());
            for (int i = 0; i < count; i++) {
                int atx = rnd.getRandom().nextInt(length) + tx;
                int aty = rnd.getRandom().nextInt(length) + ty;
                if (fg.getConditions() == null || fg.getConditions().canGenerate(tiles, world, biomeGen, atx, aty)) {
                    fg.getFeature().generate(world, tiles, atx, aty, rnd.getRandom());
                }
            }
        }
    }
}
