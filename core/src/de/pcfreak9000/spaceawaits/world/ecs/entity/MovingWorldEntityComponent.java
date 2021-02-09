package de.pcfreak9000.spaceawaits.world.ecs.entity;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class MovingWorldEntityComponent implements Component {
    public Chunk currentChunk;
}
