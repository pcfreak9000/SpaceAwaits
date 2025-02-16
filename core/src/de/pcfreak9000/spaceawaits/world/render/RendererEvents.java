package de.pcfreak9000.spaceawaits.world.render;

import de.omnikryptec.event.Event;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;

public class RendererEvents {
    
    public static class ResizeWorldRendererEvent extends Event {
        public final GameScreen renderer;
        public final int widthNew;
        public final int heightNew;
        
        public ResizeWorldRendererEvent(GameScreen renderer, int w, int h) {
            this.renderer = renderer;
            this.widthNew = w;
            this.heightNew = h;
        }
    }
    
    public static class PreFrameEvent extends Event {
        
    }
    
    @Deprecated
    public static class UpdateAnimationEvent extends Event {
        public final float dt;
        
        public UpdateAnimationEvent(float dt) {
            this.dt = dt;
        }
    }
    
    public static class OpenGuiOverlay extends Event {
        public final GuiOverlay guiOverlay;
        
        public OpenGuiOverlay(GuiOverlay guiOverlay) {
            this.guiOverlay = guiOverlay;
        }
        
    }
    
    public static class CloseGuiOverlay extends Event {
        public final GuiOverlay guiOverlay;
        
        public CloseGuiOverlay(GuiOverlay guiOverlay) {
            this.guiOverlay = guiOverlay;
        }
    }
}
