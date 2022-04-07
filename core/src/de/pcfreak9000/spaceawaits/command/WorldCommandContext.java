package de.pcfreak9000.spaceawaits.command;

import java.io.PrintWriter;

import de.pcfreak9000.spaceawaits.command.world.SeedCommand;
import de.pcfreak9000.spaceawaits.command.world.TimeCommand;
import de.pcfreak9000.spaceawaits.command.world.TpCommand;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class WorldCommandContext implements ICommandContext {
    
    private CommandlineWrapper commandline;
    
    public WorldCommandContext() {
        this.commandline = new CommandlineWrapper(new PrintWriter(System.out), new PrintWriter(System.err),
                new BaseCommand());
        addDefaultCommands();
        SpaceAwaits.BUS.post(new CoreEvents.RegisterWorldCommandsEvent(this));
    }
    
    private void addDefaultCommands() {
        getCommandline().addSubCommand(new TpCommand());
        getCommandline().addSubCommand(new GiveItemCommand());
        getCommandline().addSubCommand(new TimeCommand());
        getCommandline().addSubCommand(new SeedCommand());
    }
    
    @Override
    public void submitCommand(String string) {
        this.commandline.submitCommand(string);
    }
    
    @Override
    public CommandlineWrapper getCommandline() {
        return commandline;
    }
    
}
