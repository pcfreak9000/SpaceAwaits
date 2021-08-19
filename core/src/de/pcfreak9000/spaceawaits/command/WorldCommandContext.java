package de.pcfreak9000.spaceawaits.command;

import java.io.PrintWriter;

public class WorldCommandContext implements ICommandContext {
    
    private CommandlineWrapper commandline;
    
    public WorldCommandContext() {
        this.commandline = new CommandlineWrapper(new PrintWriter(System.out), new PrintWriter(System.err),
                new BaseCommand());
        //TMP
        getCommandline().addSubCommand(new TmpTpCommand());
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
