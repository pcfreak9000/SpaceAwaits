package mod;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsComponentInventoryShip")
public class ComponentInventoryShip implements Component, NBTSerializable {
    
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
