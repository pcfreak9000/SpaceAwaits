package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface View {
    Viewport getViewport();
    
    Camera getCamera();
    
    void updateAndRenderContent(float delta);
}
