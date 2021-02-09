package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component {

    public final Vector2 position;

    public TransformComponent() {
        this.position = new Vector2();
    }

}
