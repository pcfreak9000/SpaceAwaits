package de.pcfreak9000.spaceawaits.core;

import java.util.List;
import java.util.Random;

import de.omnikryptec.math.MathUtil;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.ISaveManager;
import de.pcfreak9000.spaceawaits.save.SaveMeta;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator.GeneratorCapabilitiesBase;

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
        ISave save = this.saveManager.createSave(name);
        Game game = new Game(save, new Player());
        game.createAndJoinWorld(
                pickGenerator(GameRegistry.GENERATOR_REGISTRY.filtered(GeneratorCapabilitiesBase.LVL_ENTRY)), "Gurke",
                0);
        this.gameCurrent = game;
    }
    
    //TMP
    private WorldGenerator pickGenerator(List<WorldGenerator> list) {
        return MathUtil.getWeightedRandom(new Random(), list);
    }
    
    public void loadGame(String uniqueSaveDesc) {
        if (!saveManager.exists(uniqueSaveDesc)) {
            throw new IllegalStateException(uniqueSaveDesc);
        }
        ISave save = this.saveManager.getSave(uniqueSaveDesc);
        Player player = new Player();
        player.readNBT(save.readPlayerNBT());//Maybe move this into Game?
        Game game = new Game(save, player);
        game.joinWorld(player.getCurrentWorld());//TMP
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
}
