package de.pcfreak9000.spaceawaits.world.ecs.content;

import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.ecs.ValidatingComponent;

public class BreakableComponent extends ValidatingComponent {
    
    @Deprecated
    public IEntityBroken entityBroken;
    
    public Breakable breakable;
}
