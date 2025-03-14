package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.content.Action;
import de.pcfreak9000.spaceawaits.gui.GuiChat;
import de.pcfreak9000.spaceawaits.world.ecs.Components;

public class ConsoleAction implements Action {
    
    @Override
    public boolean isContinuous() {
        return false;
    }
    
    @Override
    public Object getInputKey() {
        return EnumInputIds.Console;
    }
    
    @Override
    public boolean handle(float mousex, float mousey, Engine world, Entity source) {
        Player player = Components.PLAYER_INPUT.get(source).player;
        player.openContainer(new GuiChat());
        return true;
    }
    
}
