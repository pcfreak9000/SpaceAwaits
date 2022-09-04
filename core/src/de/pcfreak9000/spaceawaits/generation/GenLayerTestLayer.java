package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;

public class GenLayerTestLayer extends GenLayer {
    
    @Override
    protected Parameters[] generateSubNodes(long seed, Parameters parameters) {
        return null;
    }
    
    @Override
    protected GenLayer selectChildFor(long seed, Parameters childParams,
            ImmutableArray<GenInfo> parallelLayersSelected) {
        return null;
    }
    
}
