package de.pcfreak9000.spaceawaits.world.gen;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;

public interface IPlayerSpawn {
    @Deprecated
    Rectangle getSpawnArea(Player player);
    
    Vector2 getPlayerSpawn(Player player, World world);
}
