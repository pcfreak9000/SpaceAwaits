package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class ActivatorComponent implements Component {
    
    public final Array<Activator> activators = new Array<>();
    public float layer;
}
