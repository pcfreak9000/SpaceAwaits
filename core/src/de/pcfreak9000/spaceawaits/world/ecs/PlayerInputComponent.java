package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.core.SolidGroundContactListener;

public class PlayerInputComponent implements Component {
    public float maxXv, maxYv;
    
    public float offx, offy;
    
    public Player player;
    
    public SolidGroundContactListener solidGround;
}
