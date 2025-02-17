package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class ActionComponent implements Component {
    
    public final Array<Action> actions = new Array<>();
    
}
