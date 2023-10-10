package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.util.Recorder;
import de.pcfreak9000.spaceawaits.util.SpecialCache2D;
import de.pcfreak9000.spaceawaits.world.render.ecs.IRenderable;

public class InfiniteGeneratedTexture extends DynamicAsset implements IRenderable {
    
    private static int staticCount = 0;
    
    public static int getCachedTextureCount() {
        return staticCount;
    }
    
    private SpecialCache2D<Texture> textures;
    private int twidth, theight;
    
    private float wbypw;
    private float hbyph;
    
    private final IGenTexture gen;
    
    public InfiniteGeneratedTexture(float wbypw, float hbyph, int tw, int th, int max, int mincount, IGenTexture gen) {
        this.twidth = tw;
        this.theight = th;
        this.wbypw = wbypw;
        this.hbyph = hbyph;
        this.textures = new SpecialCache2D<>(max, mincount, (i, j) -> {
            staticCount++;
            return genArrayIndex(i, j);
        }, (t) -> {
            t.dispose();
            staticCount--;
        });
        this.gen = gen;
    }
    
    @Override
    public void createInternal() {
        gen.setup(twidth, theight);
    }
    
    private SpriteBatchImpr tmpForRebind;
    
    private Texture genArrayIndex(int i, int j) {
        int width = twidth;
        int height = theight;
        Recorder recorder = new Recorder(width, height);
        recorder.begin();
        gen.render(i, j, width, height);
        Texture t = recorder.end();
        recorder.dispose();
        if (tmpForRebind != null) {
            tmpForRebind.rebindBatchState();
        }
        return t;
    }
    
    @Override
    public void render(SpriteBatch batch, float x, float y, float rotoffx, float rotoffy, float width, float height,
            float scaleX, float scaleY, float rotation) {
        this.render(batch, x, y, 0, 0);
    }
    
    public void render(SpriteBatch batch, float x, float y, int px, int py) {
        SpriteBatchImpr batchi = (SpriteBatchImpr) batch; //Was ist denn mit casten los?
        Camera cam = batchi.getCamera();
        //Convert from cam pos into relevant texels and then directly into texture indices
        int starti = Mathf.floori(((cam.position.x - x - cam.viewportWidth / 2f) / wbypw) / (float) this.twidth);//pw/w-scale
        int startj = Mathf.floori(((cam.position.y - y - cam.viewportHeight / 2f) / hbyph) / (float) this.theight);//ph/h-scale
        int endi = Mathf.ceili(((cam.position.x + cam.viewportWidth / 2f - x) / wbypw) / (float) this.twidth);
        int endj = Mathf.ceili(((cam.position.y + cam.viewportHeight / 2f - y) / hbyph) / (float) this.theight);
        //srcxywh as given in the parameters
        int tx = px / this.twidth;
        int ty = py / this.theight;
        tmpForRebind = batchi;
        for (int i = starti; i < endi; i++) {
            for (int j = startj; j < endj; j++) {
                Texture t = this.textures.getOrFresh(i + tx, j + ty);
                int srcx = 0;//Math.max(0, px - (i + tx) * this.twidth);//in the infinite case, sry-xy becomes an offset
                int srcy = 0;//Math.max(0, py - (j + ty) * this.theight); //this causes bugs for negative i and j. Not sure if this is useful for "infinite" textures anyways
                int srcw = this.twidth;
                int srch = this.theight;
                float currentx = x + (this.twidth * i * wbypw);
                float currenty = y + (this.theight * j * hbyph);
                float currentw = srcw * wbypw;
                float currenth = srch * hbyph;
                batch.draw(t, currentx, currenty, currentw, currenth, srcx, srcy, srcw, srch, false, true);
            }
        }
        tmpForRebind = null;
    }
    
    @Override
    protected void disposeInternal() {
        this.textures.clear();
        gen.end();
    }
    
}
