package de.pcfreak9000.spaceawaits.world.gen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.player.Player;

public interface IPlayerSpawn {
    @Deprecated
    Rectangle getSpawnArea(Player player);
    
    Vector2 getPlayerSpawn(Player player, Engine world);
}
