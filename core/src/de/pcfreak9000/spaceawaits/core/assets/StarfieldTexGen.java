package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;

import de.pcfreak9000.spaceawaits.util.Util;

public class StarfieldTexGen implements IGenTexture {
    private RandomXS128 r;
    private int wi, he;
    private ShapeRenderer s;
    private long seed;
    
    private int starsTotal;
    private float scale;
    
    public StarfieldTexGen(int starsTotal, float scale) {
        this.starsTotal = starsTotal;
        this.scale = scale;
    }
    
    @Override
    public void setup(int widthTotal, int heightTotal) {
        this.wi = widthTotal;
        this.he = heightTotal;
        r = new RandomXS128();
        seed = r.nextLong();
        s = new ShapeRenderer();
    }
    
    @Override
    public void end() {
        s.dispose();
    }
    
    @Override
    public void render(int px, int py, int pw, int ph) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
        Camera cam = new OrthographicCamera(pw, ph);
        cam.translate(px + pw / 2f, py + ph / 2f, 0);
        cam.update();
        s.setProjectionMatrix(cam.combined);
        s.begin(ShapeType.Filled);
        r.setSeed(seed);
        for (int i = 0; i < starsTotal; i++) {
            float x = r.nextFloat() * wi;
            float y = r.nextFloat() * he;
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
        s.end();
    }
    
}
