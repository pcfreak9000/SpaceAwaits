package de.pcfreak9000.spaceawaits.composer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.DynamicAsset;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;

public class GeneratedTexture implements Disposable, DynamicAsset, ITextureProvider {
    
    private Texture[][] textures;
    private int tcountw, tcounth;
    private int widthTotal, heightTotal;
    private int twidth, theight;
    
    private final IGenTexture gen;
    
    private TextureRegion reg;
    
    public GeneratedTexture(int width, int height, IGenTexture gen) {
        this(width, height, width, height, gen);
    }
    
    public GeneratedTexture(int pwidth, int pheight, int tw, int th, IGenTexture gen) {
        this.widthTotal = pwidth;
        this.heightTotal = pheight;
        this.twidth = tw;
        this.theight = th;
        this.tcountw = Mathf.ceili(pwidth / (float) tw);
        this.tcounth = Mathf.ceili(pheight / (float) th);
        this.textures = new Texture[tcountw][tcounth];
        this.gen = gen;
    }
    
    public boolean supportsSingleRegion() {
        return tcountw == 1 && tcounth == 1;
    }
    
    @Override
    public TextureRegion getRegion() {
        if (!supportsSingleRegion()) {
            throw new UnsupportedOperationException();
        }
        if (this.reg == null) {
            throw new NullPointerException("Not generated");
        }
        return this.reg;
    }
    
    @Override
    public void create() {
        this.generate(gen);
    }
    
    private void generate(IGenTexture gen) {
        gen.setup(widthTotal, heightTotal);
        for (int i = 0; i < tcountw; i++) {
            for (int j = 0; j < tcounth; j++) {
                int width = Math.min((i + 1) * twidth, widthTotal) - i * twidth;
                int height = Math.min((j + 1) * theight, heightTotal) - j * theight;
                Recorder recorder = new Recorder(width, height);
                recorder.begin();
                gen.render(i * twidth, j * theight, width, height);
                textures[i][j] = recorder.end();
                recorder.dispose();
            }
        }
        gen.end();
        if (supportsSingleRegion()) {
            this.reg = new TextureRegion(this.textures[0][0]);
        }
    }
    
    public void render(SpriteBatch batch, float x, float y, float w, float h) {
        this.render(batch, x, y, w, h, 0, 0, widthTotal, heightTotal);
    }
    
    public void render(SpriteBatch batch, float x, float y, float w, float h, int px, int py, int pw, int ph) {
        if (supportsSingleRegion()) {
            batch.draw(textures[0][0], x, y, w, h, px, py, pw, ph, false, true);
            return;
        }
        int tw = Mathf.ceili(pw / (float) this.twidth);
        int th = Mathf.ceili(ph / (float) this.theight);
        int tx = px / this.twidth;
        int ty = py / this.theight;
        for (int i = 0; i < tw; i++) {
            for (int j = 0; j < th; j++) {
                Texture t = (textures[i + tx])[j + ty];
                int srcx = Math.max(0, px - (i + tx) * this.twidth);
                int srcy = Math.max(0, py - (j + ty) * this.theight);
                int srcw = Math.min(t.getWidth(), pw - (i + tx) * this.twidth);
                int srch = Math.min(t.getHeight(), ph - (j + ty) * this.theight);
                float currentx = x + w * (this.twidth * i / (float) pw);
                float currenty = y + h * (this.theight * j / (float) ph);
                float currentw = w * srcw / (float) pw;
                float currenth = h * srch / (float) ph;
                batch.draw(t, currentx, currenty, currentw, currenth, srcx, srcy, srcw, srch, false, true);
            }
        }
    }
    
    @Override
    public void dispose() {
        this.reg = null;
        if (textures != null) {
            for (int i = 0; i < textures.length; i++) {
                for (int j = 0; j < textures[i].length; j++) {
                    if ((textures[i])[j] == null) {
                        continue;
                    }
                    (textures[i])[j].dispose();
                    (textures[i])[j] = null;
                }
            }
        }
    }
    
}
