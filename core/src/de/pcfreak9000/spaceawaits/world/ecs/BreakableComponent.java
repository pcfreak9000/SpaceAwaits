package de.pcfreak9000.spaceawaits.world.ecs;

import de.pcfreak9000.spaceawaits.world.IBreakableEntity;
import de.pcfreak9000.spaceawaits.core.ecs.ValidatingComponent;
import de.pcfreak9000.spaceawaits.world.Destructible;

public class BreakableComponent extends ValidatingComponent {
    
    public IBreakableEntity breakable;
    public Destructible destructable = new Destructible();
}
