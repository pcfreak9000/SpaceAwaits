package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.util.Recorder;
import de.pcfreak9000.spaceawaits.util.SpecialCache2D;
import de.pcfreak9000.spaceawaits.world.render.ecs.IRenderable;

public class DynamicGeneratedTexture implements Disposable, DynamicAsset, ITextureProvider, IRenderable {
    
    private SpecialCache2D<Texture> textures;
    private int tcountw, tcounth;
    private int widthTotal, heightTotal;
    private int twidth, theight;
    
    private final IGenTexture gen;
    private boolean creationActive = false;
    
    private TextureRegion reg;
    
    public DynamicGeneratedTexture(int width, int height, IGenTexture gen) {
        this(width, height, width, height, gen, 1, 1);
    }
    
    public DynamicGeneratedTexture(int pwidth, int pheight, int tw, int th, IGenTexture gen, int max, int mincount) {
        this.widthTotal = pwidth;
        this.heightTotal = pheight;
        this.twidth = tw;
        this.theight = th;
        this.tcountw = Mathf.ceili(pwidth / (float) tw);
        this.tcounth = Mathf.ceili(pheight / (float) th);
        this.textures = new SpecialCache2D<>(max, mincount, (i, j) -> genArrayIndex(i, j), (t) -> t.dispose());
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
            if (!creationActive) {
                throw new IllegalStateException(
                        "Region hasn't been created and can't be created: The asset has not been created");
            }
            this.reg = new TextureRegion(this.textures.getOrFresh(0, 0));
        }
        return this.reg;
    }
    
    @Override
    public void create() {
        gen.setup(twidth, theight);
        this.creationActive = true;
    }
    
    private Texture genArrayIndex(int i, int j) {
        if (!creationActive) {
            throw new IllegalStateException("Not created");
        }
        int width = Math.min((i + 1) * twidth, widthTotal) - i * twidth;
        int height = Math.min((j + 1) * theight, heightTotal) - j * theight;
        Recorder recorder = new Recorder(width, height);
        recorder.begin();
        gen.render(i, j, width, height);
        Texture t = recorder.end();
        recorder.dispose();
        return t;
    }
    
    @Override
    public void render(SpriteBatch batch, float x, float y, float rotoffx, float rotoffy, float width, float height,
            float scaleX, float scaleY, float rotation) {
        this.render(batch, x, y, width, height, 0, 0, widthTotal, heightTotal);
    }
    
    public void render(SpriteBatch batch, float x, float y, float w, float h, int px, int py, int pw, int ph) {
        if (supportsSingleRegion()) {
            batch.draw(this.textures.getOrFresh(0, 0), x, y, w, h, px, py, pw, ph, false, true);
            return;
        }
        Camera cam = ((SpriteBatchImpr) batch).getCamera(); //Was ist denn mit casten los?
        //Convert from cam pos into relevant texels and then directly into texture indices
        int starti = Mathf.floori(((cam.position.x - x - cam.viewportWidth / 2f) * pw / w) / (float) this.twidth);
        int startj = Mathf.floori(((cam.position.y - y - cam.viewportHeight / 2f) * ph / h) / (float) this.theight);
        int endi = Mathf.ceili(((cam.position.x + cam.viewportWidth / 2f - x) * pw / w) / (float) this.twidth);
        int endj = Mathf.ceili(((cam.position.y + cam.viewportHeight / 2f - y) * ph / h) / (float) this.theight);
        //srcxywh as given in the parameters
        int tw = Mathf.ceili(pw / (float) this.twidth);
        int th = Mathf.ceili(ph / (float) this.theight);
        int tx = px / this.twidth;
        int ty = py / this.theight;
        //Combine both things
        tw = Math.min(endi, tw);
        th = Math.min(endj, th);
        starti = Math.max(starti, 0);
        startj = Math.max(startj, 0);
        for (int i = starti; i < tw; i++) {
            for (int j = startj; j < th; j++) {
                Texture t = this.textures.getOrFresh(i + tx, j + ty);
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
        creationActive = false;
        this.reg = null;
        this.textures.clear();
        gen.end();
    }
    
}
