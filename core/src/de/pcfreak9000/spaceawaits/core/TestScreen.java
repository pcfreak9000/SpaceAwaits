package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.render.SpriteBatchImpr;

public class TestScreen extends ScreenAdapter {
    
    private SpriteBatchImpr batch;
    private Texture tex;
    private Module[] noise = new Module[2];
    private Module noise1;
    
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
        //        Module noise = new ModuleBasisFunction(BasisType.SIMPLEX, InterpolationType.LINEAR, r.nextLong());
        //        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        //        source.setSource(noise);
        //        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        //        source.setSamples(10000);
        //        source.calculate2D();
        //        this.noise1 = source;
        //        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.VALUE,
        //                InterpolationType.NONE);
        //        gen.setSeed(1111);
        //        gen.setNumOctaves(2);
        //        //gen.setFrequency(0.184);
        //        gen.setLacunarity(1.5);
        //        
        //        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        //        source.setSource(gen);
        //        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        //        source.setSamples(10000);
        //        source.calculate2D();
        //        noise[0] = source;
    }
    
    private float getNoise(double x, double y) {
        double d = 0;
        //        if (noise.get(x, y) > 0) {
        //            d = noise2.get(x, y);
        //        } else {
        //            d = noise3.get(x, y);
        //        }
        for (Module m : noise) {
            double k = m.get(x, y) + 1f;
            k /= 2f;
            //k = Math.round(k * 4) / 4f;
            d += (k * 2 - 1f);
        }
        //d += noise1.get(x, y) * 0.1;
        //d /= 1.1;
        //        d=noise[0].get(noise[1].get(x, y)*0.6, noise[2].get(x, y)*0.6);
        return (float) MathUtils.clamp(d, -1, 1);
    }
    
    private static final Color[] COLORS = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN };
    
    private int toInt(float x, float y) {
        int out = 0;
        for (int i = 0; i < noise.length; i++) {
            int bit = noise[i].get(x, y) > 0 ? 1 : 0;
            out |= bit << i;
        }
        return out;
    }
    
    @Override
    public void show() {
        super.show();
        createNoise();
        this.batch = new SpriteBatchImpr(1000);
        Pixmap pix = new Pixmap(300, 300, Format.RGBA8888);
        for (int x = 0; x < pix.getWidth(); x++) {
            for (int y = 0; y < pix.getHeight(); y++) {
                pix.setColor(COLORS[toInt(x, y)]);
                pix.drawPixel(x, y);
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
