package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.List;

import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.ISaveManager;
import de.pcfreak9000.spaceawaits.save.SaveMeta;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

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
    private GameRenderer gameRenderer;
    
    private Game gameCurrent;
    
    public GameManager(ISaveManager saveMgr, GameRenderer renderer) {
        this.saveManager = saveMgr;
        this.gameRenderer = renderer;
    }
    
    public void createAndLoadGame(String name, long seed) throws IOException {
        ISave save = this.saveManager.createSave(name, seed);
        loadGame(save.getSaveMeta().getNameOnDisk());
    }
    
    public void loadGame(String uniqueSaveDesc) throws IOException {
        if (!saveManager.exists(uniqueSaveDesc)) {
            throw new IllegalStateException(uniqueSaveDesc);
        }
        ISave save = this.saveManager.getSave(uniqueSaveDesc);
        Game game = new Game(save, gameRenderer);
        game.joinGame();
        this.gameCurrent = game;
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
    
    public ISaveManager getSaveManager() {
        return this.saveManager;
    }
}
