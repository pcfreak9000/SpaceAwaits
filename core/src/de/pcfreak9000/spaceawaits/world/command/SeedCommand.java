package de.pcfreak9000.spaceawaits.world.command;

import picocli.CommandLine.Command;

@Command(name = "seed")
public class SeedCommand implements Runnable {
    @Override
    public void run() {
        //FIXME seed command System.out.println(SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent().getSeed());
    }
    
}
