package de.pcfreak9000.spaceawaits.save;

import java.util.List;

/**
 * Create, load and manage save games
 *
 */
public interface ISaveManager {
    
    //public static final int CURRENT_VERSION = 1;
    
    //Does a save already exist?
    //Create a save -> directory structure
    //What about save versions? How to handle older versions? -> introduce a version file when there actually comes nother version
    //There are multiple worlds per save and information about the player
    //Can give out some object which holds information about a save and can be used to save stuff in a WorldProvider
    //(Version converters)
    
    //In Exploration: Save: Folder with version file
    //Depending on the version, various other stuff
    
    boolean exists(String foldername);
    
    ISave createSave(String name);
    
    void deleteSave(String foldername);
    
    ISave getSave(String foldername);
    
    List<SaveMeta> listSaves();
    
}
