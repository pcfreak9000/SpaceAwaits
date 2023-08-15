package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.physics.IContactListener;

public class ContactListenerComponent implements Component {
    
    public IContactListener listener;
    
    public ContactListenerComponent() {
    }
    
    public ContactListenerComponent(IContactListener l) {
        this.listener = l;
    }
}
