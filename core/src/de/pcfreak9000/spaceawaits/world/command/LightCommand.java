package de.pcfreak9000.spaceawaits.world.command;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "light")
public class LightCommand implements Runnable {
    @Parameters(index = "0")
    public boolean light;
    
    @Override
    public void run() {
        SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent().getSystem(RenderSystem.class)
                .setDoLight(light);
    }
    
}
