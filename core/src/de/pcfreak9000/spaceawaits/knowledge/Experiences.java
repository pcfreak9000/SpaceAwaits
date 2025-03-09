package de.pcfreak9000.spaceawaits.knowledge;

import com.badlogic.gdx.utils.ObjectMap;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Experiences implements INBTSerializable {
    public static final Registry<Experience> EXPERIENCE_REGISTRY = new Registry<>();

    // TODO this needs to be serialized as well
    private ObjectMap<Experience, Object> dataholders = new ObjectMap<>();

    public <T> T getDataHolder(Experience o) {
        if (!o.hasData()) {
            throw new IllegalArgumentException();
        }
        Object dh = dataholders.get(o);
        if (dh == null) {
            EXPERIENCE_REGISTRY.checkRegistered(o);// checking on creation should be sufficient
            dh = o.createDataHolder();
            dataholders.put(o, dh);
        }
        return (T) dh;
    }

    @Override
    public void readNBT(NBTCompound nbt) {

    }

    @Override
    public void writeNBT(NBTCompound nbt) {

    }

}
