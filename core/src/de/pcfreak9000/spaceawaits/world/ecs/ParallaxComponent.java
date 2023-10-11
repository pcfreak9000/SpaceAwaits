package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

public class ParallaxComponent implements Component {
    
    /**
     * In tiles. Used to calculate the strength of the parallax effect
     */
    public float zdist;
    
    /**
     * The point where the different coordinate systems (i.e. of the world and of
     * the parallax object) match
     */
    public float xEquiv, yEquiv;
    
    //Don't touch! Also this "solution" sucks giant ass.
    public float prevxadd, prevyadd;
}
