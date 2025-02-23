package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.chunk.mgmt.ITicket;

public class ChunkTicketComponent implements Component {

    public final int radius;
    ITicket currentTicket;

    public ChunkTicketComponent(int rad) {
        this.radius = rad;
    }

}
