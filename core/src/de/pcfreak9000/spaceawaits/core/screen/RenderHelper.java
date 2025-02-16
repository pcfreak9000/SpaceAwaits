package de.pcfreak9000.spaceawaits.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyphercove.flexbatch.FlexBatch;

import de.pcfreak9000.spaceawaits.core.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.util.FrameBufferStack;

public class RenderHelper implements Disposable {
    
    private SpriteBatchImpr spriteBatch;
    private FrameBufferStack fbostack;
    
    private Viewport viewport;
    
    public RenderHelper() {
        this.spriteBatch = new SpriteBatchImpr(8191);//8191 is the max sadly...
        this.fbostack = FrameBufferStack.GLOBAL;
    }
    
    public SpriteBatchImpr getSpriteBatch() {
        return spriteBatch;
    }
    
    public FrameBufferStack getFBOStack() {
        return this.fbostack;
    }
    
    public void setViewport(Viewport vp) {
        this.viewport = vp;
    }
    
    public void applyViewport() {
        viewport.apply();
        this.spriteBatch.setCamera(viewport.getCamera());
        this.spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
    }
    
    @Override
    public void dispose() {
        this.spriteBatch.dispose();
    }
    
    //Doesn't work for the batch
    public static void setDefaultBlending() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public static void setDefaultBlending(FlexBatch<?> batch) {
        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public static void setDefaultBlending(SpriteBatch batch) {
        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
}
