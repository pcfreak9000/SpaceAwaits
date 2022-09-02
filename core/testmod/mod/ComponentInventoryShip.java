package mod;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsInventoryShip")
public class ComponentInventoryShip implements Component, INBTSerializable {
    
    public final InventoryShip invShip = new InventoryShip();
    
    @Override
    public void readNBT(NBTCompound tag) {
        invShip.readNBT(tag);
    }
    
    @Override
    public void writeNBT(NBTCompound tag) {
        invShip.writeNBT(tag);
    }
    
}
