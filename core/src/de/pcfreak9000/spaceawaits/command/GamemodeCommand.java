package de.pcfreak9000.spaceawaits.command;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.player.Player.GameMode;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "gm")
public class GamemodeCommand implements Runnable {
    @Parameters(index = "0")
    public int index;
    
    @Override
    public void run() {
        SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer()
                .setGameMode(GameMode.values()[index]);
    }
    
}
