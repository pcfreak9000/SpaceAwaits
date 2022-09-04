package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

public abstract class GenLayer {
    
    //public abstract boolean isAcceptable(GenLayer parent, Parameters parameters,
    //      ImmutableArray<GenInfo> parallelLayersSelected);
    
    protected abstract Parameters[] generateSubNodes(long seed, Parameters parameters);
    
    protected abstract GenLayer selectChildFor(long seed, Parameters childParams,
            ImmutableArray<GenInfo> parallelLayersSelected);
    
    public GenInfo[] generate(long seed, Parameters parameters) {
        Parameters[] childParams = generateSubNodes(seed, parameters);
        Array<GenInfo> sublayers = new Array<>();
        ImmutableArray<GenInfo> sublayersImmutable = new ImmutableArray<>(sublayers);
        for (Parameters p : childParams) {
            GenLayer child = selectChildFor(seed, parameters, sublayersImmutable);
            sublayers.add(new GenInfo(child, p));
        }
        return sublayers.items;
    }
}
