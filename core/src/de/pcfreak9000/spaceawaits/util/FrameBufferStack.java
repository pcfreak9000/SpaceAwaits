package de.pcfreak9000.spaceawaits.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FrameBufferStack {
    
    public static final FrameBufferStack GLOBAL = new FrameBufferStack();
    
    private final Deque<FrameBuffer> fbstack = new ArrayDeque<>();
    
    private FrameBufferStack() {
        
    }
    
    public void push(FrameBuffer fb) {
        if (fbstack.peek() != fb) {
            fbstack.push(fb);
            fb.begin();
        }
    }
    
    public void pop(FrameBuffer fbo) {
        if (fbo != fbstack.peek()) {
            throw new IllegalStateException();
        }
        FrameBuffer fb = fbstack.pop();
        if (fbstack.isEmpty()) {
            fb.end();
        } else {
            fb.end();
            fbstack.peek().begin();
        }
    }
    
    public void drawAll(SpriteBatch batch, Camera cam) {
        Iterator<FrameBuffer> it = this.fbstack.descendingIterator();
        while (it.hasNext()) {
            FrameBuffer next = it.next();
            batch.draw(next.getColorBufferTexture(), cam.position.x - cam.viewportWidth / 2,
                    cam.position.y - cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight, 0, 0,
                    next.getWidth(), next.getHeight(), false, true);
        }
    }
    
    public void rebind() {
        fbstack.peek().begin();
    }
}
