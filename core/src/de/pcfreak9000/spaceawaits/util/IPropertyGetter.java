package de.pcfreak9000.spaceawaits.util;

public interface IPropertyGetter<T extends IStepWiseComponent> {
    float getValue(int x, T swcomp);
}
