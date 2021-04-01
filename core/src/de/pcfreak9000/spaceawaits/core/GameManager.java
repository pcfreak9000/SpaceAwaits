package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.List;

import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.ISaveManager;
import de.pcfreak9000.spaceawaits.save.SaveMeta;

/**
 * Basiacally the backend to a level selector. Creates new gamesaves, and loads
 * them from the disk, knows about existing gamesaves.
 * 
 * @author pcfreak9000
 *
 */
public class GameManager {
    
    //Game creation/the backend to a level selector
    //Also game unloading
    
    private ISaveManager saveManager;
    
    private Game gameCurrent;
    
    public GameManager(ISaveManager saveMgr) {
        this.saveManager = saveMgr;
    }
    
    public void createAndLoadGame(String name, long seed) {
        ISave save = this.saveManager.createSave(name, seed);
        loadGame(save.getSaveMeta().getNameOnDisk());
    }
    
    public void loadGame(String uniqueSaveDesc) {
        if (!saveManager.exists(uniqueSaveDesc)) {
            throw new IllegalStateException(uniqueSaveDesc);
        }
        try {
            ISave save = this.saveManager.getSave(uniqueSaveDesc);
            Game game = new Game(save);
            game.joinGame();
            this.gameCurrent = game;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void unloadGame() {
        if (getGameCurrent() == null) {
            throw new IllegalStateException();
        }
        this.gameCurrent.saveAndLeaveCurrentWorld();
        this.gameCurrent = null;
    }
    
    public List<SaveMeta> listSaves() {
        return saveManager.listSaves();
    }
    
    public Game getGameCurrent() {
        return this.gameCurrent;
    }
}
