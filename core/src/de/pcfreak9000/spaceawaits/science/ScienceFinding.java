package de.pcfreak9000.spaceawaits.science;

import java.util.Objects;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class ScienceFinding implements INBTSerializable {
    private ScienceType type;
    private String id;
    
    //for (de)serialization
    ScienceFinding() {
    }
    
    public ScienceFinding(ScienceType type, String id) {
        ScienceType.REGISTRY.checkRegistered(type);
        this.type = type;
        this.id = id;
    }
    
    
    
    @Override
    public void readNBT(NBTCompound nbt) {
        nbt.putString("typeid", ScienceType.REGISTRY.getId(type));
        nbt.putString("id", id);
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        type = ScienceType.REGISTRY.getOrDefault(nbt.getString("typeid"), null);
        id = nbt.getString("id");
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScienceFinding other = (ScienceFinding) obj;
        return Objects.equals(id, other.id) && Objects.equals(type, other.type);
    }
}
