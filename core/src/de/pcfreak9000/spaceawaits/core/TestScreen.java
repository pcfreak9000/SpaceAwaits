package de.pcfreak9000.spaceawaits.core;

import java.util.Random;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.omnikryptec.math.Mathd;
import de.pcfreak9000.spaceawaits.util.IPropertyGetter;
import de.pcfreak9000.spaceawaits.util.IStepwise1D;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.render.SpriteBatchImpr;

public class TestScreen extends ScreenAdapter {
    
    private SpriteBatchImpr batch;
    private Texture tex;
    private Module[] noise = new Module[2];
    private Module noise1;
    
    private Random random = new RandomXS128();
    
    private double randomAt(double x, double y) {
        int xint = Mathd.floori(x);
        int yint = Mathd.floori(y);
        
        int samplex = xint;
        int sampley = yint;
        double curDist2 = Double.POSITIVE_INFINITY;
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                random.setSeed(RndHelper.getSeedAt(555534, xint + i, yint + j));
                double xpos = xint + i + random.nextDouble() * 2.0 - 1;
                random.setSeed(RndHelper.getSeedAt(555534 + 1, xint + i, yint + j));
                double ypos = yint + j + random.nextDouble() * 2.0 - 1;
                double dist2 = Mathd.square(xpos - x) + Mathd.square(ypos - y);
                if (dist2 < curDist2) {
                    curDist2 = dist2;
                    samplex = Mathd.floori(xpos);
                    sampley = Mathd.floori(ypos);
                }
            }
        }
        random.setSeed(RndHelper.getSeedAt(4563, samplex, sampley));
        return random.nextDouble() * 2.0 - 1.0;
    }
    
    private float randomAt(double x) {
        int xint = Mathd.floori(x);
        
        int samplex = xint;
        double curDist = Double.POSITIVE_INFINITY;
        for (int i = -3; i <= 3; i++) {
            random.setSeed(RndHelper.getSeedAt(555534, xint + i));
            double xpos = xint + i + random.nextDouble() * 2.0 - 1.0;
            double dist = Mathd.abs(xpos - x);
            if (dist < curDist) {
                curDist = dist;
                samplex = Mathd.floori(xpos);
            }
        }
        random.setSeed(RndHelper.getSeedAt(3, samplex));
        return (float) (random.nextDouble() * 2.0 - 1.0);
        
    }
    
    private void createNoise() {
        RandomXS128 r = new RandomXS128(1111);
        for (int i = 0; i < noise.length; i++) {
            ModuleFractal noise = new ModuleFractal(FractalType.FBM, BasisType.VALUE, InterpolationType.QUINTIC);
            noise.setNumOctaves(5);
            //noise.setLacunarity(1.5);
            // noise.setGain(0.06);
            noise.setFrequency(0.03f);
            noise.setSeed(r.nextLong());
            ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
            source.setSource(noise);
            source.setSampleScale(Chunk.CHUNK_SIZE * 2);
            source.setSamples(10000);
            source.calculate2D();
            this.noise[i] = source;
        }
        
    }
    
    private static final Color[] COLORS = { Color.MAGENTA, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
            Color.BLUE, Color.CHARTREUSE, Color.BROWN, Color.CYAN, Color.FIREBRICK };
    
    private static final int interpconstmax = 30;
    
    private static final double size = 80.0;
    
    private double lim = 0.5;
    
    @Override
    public void show() {
        super.show();
        createNoise();
        TestStepwiseComponent a = new TestStepwiseComponent(Interpolation.smooth, 30);
        TestStepwiseComponent b = new TestStepwiseComponent(Interpolation.bounce, 30);
        IStepwise1D<TestStepwiseComponent> stepwise = (x) -> randomAt(x / size) <= lim ? a : b;
        IPropertyGetter<TestStepwiseComponent> propget = (xyz, swcomp) -> randomAt(xyz / size);
        //IntFunction<Interpolation> interpol = (x) -> interpol(x / size);
        //IntUnaryOperator interpconst = (x) -> interpconst(x / size);
        this.batch = new SpriteBatchImpr(1000);
        Pixmap pix = new Pixmap(400, 400, Format.RGBA8888);
        for (int x = 0; x < pix.getWidth(); x++) {
            float d1 = Util.interpolateStepwise(x, stepwise, propget, Interpolation.linear, interpconstmax) * 0.5f
                    + 0.5f;
            for (int y = 0; y < pix.getHeight(); y++) {
                if (y > d1 * pix.getHeight()) {
                    continue;
                }
                double d = randomAt(x / 20.0, y / 20.0);
                d = d * 0.5 + 0.5;
                //int index = Mathd.floori(d * COLORS.length);
                if (randomAt(x / size) <= lim) {
                    pix.setColor(1, (float) d, (float) d, 1);
                } else {
                    pix.setColor((float) d, (float) d, (float) d, 1);
                }
                //pix.setColor(COLORS[index]);
                pix.drawPixel(x, y);
                //pix.setColor(COLORS[toInt(x, y)]);
                //pix.drawPixel(x, y);
                //                float n = getNoise(x / 20f, y / 20f);
                //                n = (n + 1f) / 2f;
                //                for (int i = 0; i < COLORS.length; i++) {
                //                    if (n <= (i + 1) / (float) COLORS.length) {
                //                        pix.setColor(COLORS[i]);
                //                        pix.drawPixel(x, y);
                //                        break;
                //                    }
                //                }
            }
        }
        tex = new Texture(pix);
        pix.dispose();
    }
    
    @Override
    public void render(float delta) {
        super.render(delta);
        ScreenUtils.clear(0, 0, 0, 0);
        if (tex != null) {
            batch.begin();
            batch.draw(tex, 0, 0, 400, 400);
            batch.end();
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        tex.dispose();
    }
}
