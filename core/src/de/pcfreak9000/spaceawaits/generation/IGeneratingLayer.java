package de.pcfreak9000.spaceawaits.generation;

public interface IGeneratingLayer<T, P extends Parameters> {
    
    T generate(P params);
}
