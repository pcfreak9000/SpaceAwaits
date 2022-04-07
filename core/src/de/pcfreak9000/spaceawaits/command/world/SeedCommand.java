package de.pcfreak9000.spaceawaits.command.world;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import picocli.CommandLine.Command;

@Command(name = "seed")
public class SeedCommand implements Runnable {
    @Override
    public void run() {
        System.out.println(SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent().getSeed());
    }
    
}
