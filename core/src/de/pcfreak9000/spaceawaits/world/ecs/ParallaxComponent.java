package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

public class ParallaxComponent implements Component {
    
    public float zdist;
    
    public float xOffset, yOffset;
    
    //Don't touch! Also this "solution" sucks giant ass.
    public float prevxadd, prevyadd;
}
