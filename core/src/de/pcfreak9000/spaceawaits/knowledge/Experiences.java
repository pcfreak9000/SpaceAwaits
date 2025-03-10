package de.pcfreak9000.spaceawaits.knowledge;

import com.badlogic.gdx.utils.ObjectMap;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.AnnotationSerializer;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Experiences implements INBTSerializable {
    public static final Registry<Experience> EXPERIENCE_REGISTRY = new Registry<>();

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
        NBTCompound datacompound = nbt.getCompound("data");
        for (String id : datacompound.keySet()) {
            if (!EXPERIENCE_REGISTRY.isRegistered(id)) {
                continue;
            }
            Experience k = EXPERIENCE_REGISTRY.get(id);
            if (!k.hasData()) {
                continue;
            }
            Object o = k.createDataHolder();
            if (!AnnotationSerializer.canAnnotationSerialize(o)) {
                continue;
            }
            NBTCompound data = datacompound.getCompound(id);
            AnnotationSerializer.deserialize(o, data);
        }

    }

    @Override
    public void writeNBT(NBTCompound nbt) {
        NBTCompound datacompound = new NBTCompound();
        for (ObjectMap.Entry<Experience, Object> e : dataholders.entries()) {
            if (!AnnotationSerializer.canAnnotationSerialize(e.value)) {
                continue;
            }
            NBTCompound data = AnnotationSerializer.serialize(e.value);
            datacompound.putCompound(EXPERIENCE_REGISTRY.getId(e.key), data);
        }
        nbt.putCompound("data", datacompound);
    }

}
