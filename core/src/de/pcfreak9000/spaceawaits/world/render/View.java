package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.command.ICommandContext;

public interface View {
    Viewport getViewport();
    
    Camera getCamera();
    
    ICommandContext getCommandContext();
    
    void updateAndRenderContent(float delta, boolean gui);
}
