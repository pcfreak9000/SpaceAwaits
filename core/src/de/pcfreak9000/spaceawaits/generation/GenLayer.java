package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

public abstract class GenLayer<T> implements IGeneratingLayer {
    
    //public abstract boolean isAcceptable(GenLayer parent, Parameters parameters,
    //      ImmutableArray<GenInfo> parallelLayersSelected);
    
    protected abstract Parameters[] generateSubNodes(long seed, Parameters parameters);
    
    protected abstract T selectChildFor(long seed, Parameters childParams,
            ImmutableArray<GenInfo> parallelLayersSelected);
    
    //public abstract G generateFlat(long seed, Parameters parameters);
    
    @Override
    public Object generate(long seed, Parameters parameters) {
        Parameters[] childParams = generateSubNodes(seed, parameters);
        Array<GenInfo> sublayers = new Array<>(true, childParams.length, GenInfo.class);
        ImmutableArray<GenInfo> sublayersImmutable = new ImmutableArray<>(sublayers);
        for (Parameters p : childParams) {
            T child = selectChildFor(seed, parameters, sublayersImmutable);
            sublayers.add(new GenInfo(child, p));
        }
        return sublayers.items;
    }
}
