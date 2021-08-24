package de.pcfreak9000.spaceawaits.composer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class Composer {
    
    private int width;
    private int height;
    
    public Composer(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public ComposedImage compose() {
        FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, width, height, false);
        fbo.begin();
        render();
        Pixmap p = Pixmap.createFromFrameBuffer(0, 0, width, height);
        fbo.end();
        Texture t = new Texture(p);
        p.dispose();
        fbo.dispose();
        return new ComposedImage(width, height, t);
    }
    
    protected void render() {
        
    }
}