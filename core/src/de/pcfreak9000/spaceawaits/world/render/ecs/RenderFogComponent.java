package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class RenderFogComponent implements Component {
    public float width, height;
    public boolean oversize;
    public Rectangle innerRect;
    public Color color = Color.WHITE;
    public float velx = 0, vely = 0;
    public float scalex = 1, scaley = 1;
    public float coeffa = 1, coeffb = 0;
    public float fbmVelx = 0, fbmVely = 0;
}
