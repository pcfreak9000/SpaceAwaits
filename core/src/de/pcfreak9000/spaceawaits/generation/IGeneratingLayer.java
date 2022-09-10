package de.pcfreak9000.spaceawaits.generation;

public interface IGeneratingLayer<T, P> {
    
    T generate(P params);
}
