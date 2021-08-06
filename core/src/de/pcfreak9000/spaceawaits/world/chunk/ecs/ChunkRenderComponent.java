package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Component;
@Deprecated
public class ChunkRenderComponent implements Component {
    
    public boolean dirty;
    public int len;
    public int blen;
    public int cid = -1;
}
