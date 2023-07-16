package de.pcfreak9000.spaceawaits.util;

public interface IPropertyGetter2D<T extends IStepWiseComponent> {
    float getValue(int x, int y, T swcomp);
    
}
