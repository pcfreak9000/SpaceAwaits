package de.pcfreak9000.spaceawaits.world.ecs.content;

import de.pcfreak9000.spaceawaits.world.IBreakableEntity;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.ecs.ValidatingComponent;

public class BreakableComponent extends ValidatingComponent {
    
    public IBreakableEntity breakable;
    public Destructible destructable = new Destructible();
}
