package de.pcfreak9000.spaceawaits.command.world;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "time")
public class TimeCommand implements Runnable {
    
    @Parameters
    private long newtime;
    
    @Override
    public void run() {
        SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent().time = newtime;
    }
    
}
