package de.pcfreak9000.spaceawaits.util;

public interface IPropertyGetter<T extends IStepWiseComponent> {
    double applyAsDouble(int x, IStepwise1D<T> stepwise);
}
