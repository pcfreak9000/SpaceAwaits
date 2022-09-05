package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
//This class may be useless and its probably overusing generics anyways
public abstract class GenLayer<T, P extends Parameters, C extends Parameters>
        implements IGeneratingLayer<GenInfo[], P> {
    
    protected abstract C[] generateSubNodes(P parameters);
    
    protected abstract T getChildFor(P myparams, C childParams, ImmutableArray<GenInfo> parallelLayersSelected);
    
    @Override
    public GenInfo[] generate(P parameters) {
        C[] childParams = generateSubNodes(parameters);
        Array<GenInfo> sublayers = new Array<>(true, childParams.length, GenInfo.class);
        ImmutableArray<GenInfo> sublayersImmutable = new ImmutableArray<>(sublayers);
        for (C p : childParams) {
            T child = getChildFor(parameters, p, sublayersImmutable);
            sublayers.add(new GenInfo(child, p));
        }
        return sublayers.items;
    }
}
