package de.pcfreak9000.spaceawaits.util;

import java.util.ArrayDeque;
import java.util.Deque;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FrameBufferStack {
    
    private final Deque<FrameBuffer> fbstack = new ArrayDeque<>();
    
    public void push(FrameBuffer fb) {
        if (fbstack.peek() != fb) {
            fbstack.push(fb);
            fb.begin();
        }
    }
    
    public void pop() {
        FrameBuffer fb = fbstack.pop();
        if (fbstack.isEmpty()) {
            fb.end();
        } else {
            fbstack.peek().begin();
        }
    }
    
}
