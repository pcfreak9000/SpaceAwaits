package de.pcfreak9000.spaceawaits.comp;

import java.util.Map.Entry;

import com.badlogic.gdx.utils.ObjectFloatMap;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
@Deprecated
public class CompositeInventory implements NBTSerializable {
    
    private ObjectFloatMap<Composite> storage = new ObjectFloatMap<>();
    
    public boolean hasComposite(Composite name) {
        return storage.get(name, 0) > 0;
    }
    
    public float getCompositeAmount(Composite name) {
        return storage.get(name, 0);
    }
    
    public void putComposite(Composite name, float amount) {
        //TODO check if that composite exists first
        float current = storage.get(name, 0);
        storage.put(name, amount + current);
    }
    
    public void print() {
        System.out.println(storage.toString());
    }
    
    @Override
    public void readNBT(NBTTag tag) {
        CompositeManager compMgr = Registry.COMPOSITE_MANAGER;
        NBTCompound comp = (NBTCompound) tag;
        for (Entry<String, NBTTag> e : comp.entrySet()) {
            Composite c = compMgr.getCompositeForName(e.getKey());
            if (c != null) {
                storage.put(c, comp.getFloat(e.getKey()));
            }
        }
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound comp = new NBTCompound();
        for (Composite c : storage.keys()) {
            comp.putFloat(c.getId(), getCompositeAmount(c));
        }
        return comp;
    }
}
