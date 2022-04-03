package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.gui.GuiChat;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Action;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputComponent;

public class ConsoleAction implements Action {
    private static final ComponentMapper<PlayerInputComponent> mapper = ComponentMapper
            .getFor(PlayerInputComponent.class);
    
    @Override
    public Object getInputKey() {
        return EnumInputIds.Console;
    }
    
    @Override
    public boolean handle(float mousex, float mousey, World world, Entity source) {
        Player player = mapper.get(source).player;
        player.openContainer(new GuiChat());
        return true;
    }
    
}
