package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.Event;

public class RendererEvents {
    
    public static class ResizeWorldRendererEvent extends Event {
        public final WorldRenderer renderer;
        public final int widthNew;
        public final int heightNew;
        
        public ResizeWorldRendererEvent(WorldRenderer renderer, int w, int h) {
            this.renderer = renderer;
            this.widthNew = w;
            this.heightNew = h;
        }
    }
    
}
