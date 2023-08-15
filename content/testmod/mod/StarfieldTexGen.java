package mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;

import de.pcfreak9000.spaceawaits.composer.IGenTexture;
import de.pcfreak9000.spaceawaits.util.Util;

public class StarfieldTexGen implements IGenTexture {
    private RandomXS128 r;
    private int wi, he;
    private ShapeRenderer s;
    private long seed;
    
    @Override
    public void setup(int widthTotal, int heightTotal) {
        this.wi = widthTotal;
        this.he = heightTotal;
        r = new RandomXS128();
        seed = r.nextLong();
        s = new ShapeRenderer();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    @Override
    public void end() {
        s.dispose();
    }
    
    @Override
    public void render(int px, int py, int pw, int ph) {
        Camera cam = new OrthographicCamera(pw, ph);
        cam.translate(px + pw / 2f, py + ph / 2f, 0);
        cam.update();
        s.setProjectionMatrix(cam.combined);
        s.begin(ShapeType.Filled);
        r.setSeed(seed);
        for (int i = 0; i < 20000; i++) {
            float x = r.nextFloat() * wi;
            float y = r.nextFloat() * he;
            float radius = 0.0006f * (0.75f + r.nextFloat());
            radius *= 2048;
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
