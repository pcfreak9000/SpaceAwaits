package de.pcfreak9000.spaceawaits.world.breaking.ecs;

import de.pcfreak9000.spaceawaits.core.ecs.ValidatingComponent;
import de.pcfreak9000.spaceawaits.world.breaking.BreakableInfo;
import de.pcfreak9000.spaceawaits.world.breaking.IBreakable;

public class BreakableComponent extends ValidatingComponent {
    
    public IBreakable breakable;
    public BreakableInfo destructable = new BreakableInfo();
}
