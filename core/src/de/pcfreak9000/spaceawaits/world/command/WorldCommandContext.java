package de.pcfreak9000.spaceawaits.world.command;

import java.io.PrintWriter;

import de.pcfreak9000.spaceawaits.command.BaseCommand;
import de.pcfreak9000.spaceawaits.command.CommandlineWrapper;
import de.pcfreak9000.spaceawaits.command.GamemodeCommand;
import de.pcfreak9000.spaceawaits.command.GiveItemCommand;
import de.pcfreak9000.spaceawaits.command.ICommandContext;
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
        getCommandline().addSubCommand(new SeedCommand());
        getCommandline().addSubCommand(new GamemodeCommand());
        getCommandline().addSubCommand(new LightCommand());
        getCommandline().addSubCommand(new GhostCommand());
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
