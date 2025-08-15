package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.gui.GuiChat;
@RenderSystemMarker
public class GuiChatSystem extends EntitySystem {
    @Override
    public void update(float deltaTime) {
        if (!getEngine().getSystem(GuiOverlaySystem.class).isGuiContainerOpen()
                && InptMgr.UI.isJustPressed(EnumInputIds.Console)) {
            GuiChat g = new GuiChat();
            g.createAndOpen(null);
        }
    }
}
