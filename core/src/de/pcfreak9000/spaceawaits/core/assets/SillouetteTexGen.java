package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import de.pcfreak9000.spaceawaits.generation.IGenInt1D;

public class SillouetteTexGen implements IGenTexture {
    
    private IGenInt1D heightGen;
    
    private int patchWidthMax, patchHeightMax;
    private ShapeRenderer s;
    
    //TODO Could do some interpolation stuff for IGenInt1D, so the same generators as for terrain could be used generating the same mountains but at a higher resolution????
    //the above might not(!) work nicely
    public SillouetteTexGen(IGenInt1D heightGen) {
        this.heightGen = heightGen;
    }
    
    @Override
    public void setup(int patchWidthMax, int patchHeightMax) {
        this.patchWidthMax = patchWidthMax;
        this.patchHeightMax = patchHeightMax;
        s = new ShapeRenderer();
    }
    
    @Override
    public void end() {
        s.dispose();
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
        s.setColor(Color.GRAY);
        for (int i = 0; i < width; i++) {
            int x = pi * patchWidthMax + i;
            int h = this.heightGen.generate(x);
            if (h >= pj * patchHeightMax) {
                s.rect(x, pj * patchHeightMax, 1, h - pj * patchHeightMax);
            }
        }
        s.end();
    }
    
}
