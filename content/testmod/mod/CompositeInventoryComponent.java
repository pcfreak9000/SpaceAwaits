package mod;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.comp.CompositeInventory;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsCompositeInventoryComponent")
public class CompositeInventoryComponent implements Component, NBTSerializable {
    
    public CompositeInventory compositeInv;
    
    @Override
    public void readNBT(NBTTag tag) {
        if (compositeInv == null) {
            compositeInv = new CompositeInventory();
        }
        compositeInv.readNBT(tag);
    }
    
    @Override
    public NBTTag writeNBT() {
        return compositeInv == null ? new NBTCompound() : compositeInv.writeNBT();
    }
    
}
