package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.player.Player;

public class PlayerInputComponent implements Component {
    public float maxXv, maxYv;
    
    public float offx, offy;
    
    public Player player;
    
}
