package de.pcfreak9000.spaceawaits.core;

import de.pcfreak9000.spaceawaits.tileworld.World;
import de.pcfreak9000.spaceawaits.tileworld.WorldLoadingBounds;
import de.pcfreak9000.spaceawaits.tileworld.WorldManager;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TransformComponent;

/**
 * The currently loaded level. Information about the player, the world, and
 * world generation.
 *
 * @author pcfreak9000
 *
 */
public class GameInstance {
    
    private final Player player;
    
    private final WorldManager groundManager;
    
    public GameInstance(WorldManager gmgr) {
        this.groundManager = gmgr;
        this.player = new Player(); //TODO playerstats creation
    }
    
    public void visit(World world, float x, float y) {
        //TODO set player coords
        
        TransformComponent tc = this.player.getPlayerEntity().getComponent(TransformComponent.class);
        tc.position.set(x, y);
        this.groundManager.getLoader().setWorldUpdateFence(new WorldLoadingBounds(tc.position));
        this.groundManager.getECSManager().addEntity(this.player.getPlayerEntity());
        this.groundManager.setWorld(world);
    }
    
}
