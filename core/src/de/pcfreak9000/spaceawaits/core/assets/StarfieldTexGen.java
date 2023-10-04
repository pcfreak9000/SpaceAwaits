package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;

import de.pcfreak9000.spaceawaits.generation.RndHelper;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.Util;

public class StarfieldTexGen implements IGenTexture {
    private RandomXS128 r;
    private int patchWidthMax, patchHeightMax;
    private ShapeRenderer s;
    private long seed;
    
    private int starsPerPatch;
    private float scale;
    
    public StarfieldTexGen(int starsTotal, float scale) {
        this.starsPerPatch = starsTotal;
        this.scale = scale;
    }
    
    @Override
    public void setup(int patchWidthMax, int patchHeightMax) {
        this.patchWidthMax = patchWidthMax;
        this.patchHeightMax = patchHeightMax;
        r = new RandomXS128();
        seed = r.nextLong();
        s = new ShapeRenderer();
    }
    
    @Override
    public void end() {
        s.dispose();
    }
    
    private void renderPatch(int pi, int pj, Camera cam) {
        r.setSeed(RndHelper.getSeedAt(seed, pi, pj));
        for (int i = 0; i < starsPerPatch; i++) {
            float x = r.nextFloat() * patchWidthMax + pi * patchWidthMax;
            float y = r.nextFloat() * patchHeightMax + pj * patchHeightMax;
            float radius = 0.0006f * (0.75f + r.nextFloat());
            radius *= scale;
            float colorIndex = r.nextFloat();
            if (!cam.frustum.sphereInFrustum(x, y, 0, radius)) {
                continue;
            }
            Color c = Util.ofTemperature(37500 * colorIndex + 2500);
            s.setColor(c);
            s.circle(x, y, radius, 20);
        }
    }
    
    @Override
    public void render(int pi, int pj, int width, int height) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
        Camera cam = new OrthographicCamera(width, height);
        cam.translate(pi * patchWidthMax + width / 2f, pj * patchHeightMax + height / 2f, 0);
        cam.update();
        s.setProjectionMatrix(cam.combined);
        s.begin(ShapeType.Filled);
        for (Direction d : Direction.MOORE_ZERO) {
            renderPatch(pi + d.dx, pj + d.dy, cam);
        }
        s.end();
    }
    
}
