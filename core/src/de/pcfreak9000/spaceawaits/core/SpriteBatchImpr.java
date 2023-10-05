package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteBatchImpr extends SpriteBatch {
    
    private Camera camera;
    
    public SpriteBatchImpr(int i) {
        super(i);
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public void rebindBatchState() {
        getShader().bind();
        Gdx.gl.glDepthMask(false);//Maybe add temp unbind to set depthmask to true, if other stuff wants to use that but doesn't explicitly set that???
    }
    
    public void setAdditiveBlending() {
        this.enableBlending();
        this.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
    }
    
    public void setDefaultBlending() {
        this.enableBlending();
        this.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void setMultiplicativeBlending() {
        this.enableBlending();
        this.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
    }
    
    public void resetSettings() {
        setDefaultBlending();
        setColor(Color.WHITE);
    }
}
