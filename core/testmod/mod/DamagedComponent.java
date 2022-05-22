package mod;
import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class DamagedComponent implements Component, NBTSerializable {

    //<=0: no damage, >0: damaged
    public double damage;
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTCompound nbtc = (NBTCompound) tag;
        damage = nbtc.getDouble("damage");
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound comp = new NBTCompound();//what a waste of space
        comp.putDouble("damage", damage);
        return comp;
    }
    
}
