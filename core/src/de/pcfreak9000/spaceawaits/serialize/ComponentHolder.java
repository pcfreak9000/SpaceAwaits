package de.pcfreak9000.spaceawaits.serialize;

import com.badlogic.ashley.core.Component;

public interface ComponentHolder {
    
    Component getComponent(Class<? extends Component> clazz);
    
    void add(Component comp);
}
