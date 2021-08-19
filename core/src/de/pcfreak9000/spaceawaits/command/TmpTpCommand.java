package de.pcfreak9000.spaceawaits.command;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "tp")
public class TmpTpCommand implements Runnable {
    @Parameters(index = "0")
    private float targetx;
    @Parameters(index = "1")
    private float targety;
    
    @Override
    public void run() {
        SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer().getPlayerEntity()
                .getComponent(TransformComponent.class).position.set(targetx, targety);
    }
}
