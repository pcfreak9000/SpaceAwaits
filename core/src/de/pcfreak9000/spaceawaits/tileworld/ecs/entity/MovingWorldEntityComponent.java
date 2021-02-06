package de.pcfreak9000.spaceawaits.tileworld.ecs.entity;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;

public class MovingWorldEntityComponent implements Component {
    public Chunk currentChunk;
}
