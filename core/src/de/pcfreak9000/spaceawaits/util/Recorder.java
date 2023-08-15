package de.pcfreak9000.spaceawaits.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

public class Recorder implements Disposable {
    
    private FrameBuffer fbo;
    private int w, h;
    private boolean recording;
    
    public Recorder(int w, int h) {
        this.w = w;
        this.h = h;
        this.fbo = new FrameBuffer(Format.RGBA8888, w, h, false);
    }
    
    public void begin() {
        if (recording) {
            throw new IllegalStateException();
        }
        fbo.begin();
        ScreenUtils.clear(0, 0, 0, 0);
        recording = true;
    }
    
    public Texture end() {
        if (!recording) {
            throw new IllegalStateException();
        }
        Pixmap pix = Pixmap.createFromFrameBuffer(0, 0, w, h);
        fbo.end();
        Texture t = new Texture(pix);
        pix.dispose();
        recording = false;
        return t;
    }
    
    @Override
    public void dispose() {
        if (recording) {
            throw new IllegalStateException();
        }
        if (fbo != null) {
            fbo.dispose();
        }
    }
}
