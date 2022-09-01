package de.pcfreak9000.spaceawaits.serialize;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.utils.ImmutableArray;

public interface ComponentHolder {
    
    ImmutableArray<Component> getComponents();
    
    Component getComponent(Class<? extends Component> clazz);
    
    void add(Component comp);
}
