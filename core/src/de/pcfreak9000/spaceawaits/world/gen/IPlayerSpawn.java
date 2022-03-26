package de.pcfreak9000.spaceawaits.world.gen;

import com.badlogic.gdx.math.Rectangle;

import de.pcfreak9000.spaceawaits.player.Player;

public interface IPlayerSpawn {
    
    Rectangle getSpawnArea(Player player);
    
}
