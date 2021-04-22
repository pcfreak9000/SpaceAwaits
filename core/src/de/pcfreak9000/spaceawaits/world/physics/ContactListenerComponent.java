package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Component;

public class ContactListenerComponent implements Component {
    
    public IContactListener listener;
    
    public ContactListenerComponent() {
    }
    
    public ContactListenerComponent(IContactListener l) {
        this.listener = l;
    }
}
