package de.pcfreak9000.spaceawaits.command;

public interface ICommandContext {
    
    void submitCommand(String string);
    
    CommandlineWrapper getCommandline();
    
}
