package de.pcfreak9000.spaceawaits.world.ecs.chunk;

import com.badlogic.ashley.core.Component;

public class ChunkRenderComponent implements Component {
    
    public boolean dirty;
    public int len;
    public int blen;
    public int cid = -1;
}
