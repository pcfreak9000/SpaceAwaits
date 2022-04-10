import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class ComponentInventoryShip implements Component, NBTSerializable {
    static {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsComponentInventoryShip",
                ComponentInventoryShip.class);
    }
    public final InventoryShip invShip = new InventoryShip();
    
    @Override
    public void readNBT(NBTTag tag) {
        invShip.readNBT(tag);
    }
    
    @Override
    public NBTTag writeNBT() {
        return invShip.writeNBT();
    }
    
}
