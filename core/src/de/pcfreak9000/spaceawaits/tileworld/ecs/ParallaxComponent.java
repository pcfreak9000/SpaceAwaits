package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.Component;

public class ParallaxComponent implements Component {

    public final float xMov;
    public final float yMov;
    public final float aspect;

    public ParallaxComponent(float xMov, float yMov, float aspect) {
        this.xMov = xMov;
        this.yMov = yMov;
        this.aspect = aspect;
    }

}
