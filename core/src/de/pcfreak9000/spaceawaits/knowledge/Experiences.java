package de.pcfreak9000.spaceawaits.knowledge;

import com.badlogic.gdx.utils.ObjectMap;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.AnnotationSerializer;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Experiences implements INBTSerializable {
    public static final Registry<Experience> EXPERIENCE_REGISTRY = new Registry<>();
    
    private ObjectMap<Experience, Object> dataholders = new ObjectMap<>();
    private EventBus bus = new EventBus();
    private EventBus parent;
    
    public void register(EventBus parent) {
        for (Experience k : EXPERIENCE_REGISTRY.getAll()) {
            if (dataholders.containsKey(k)) {
                continue;
            }
            Object dh = k.createDataHolder();
            dataholders.put(k, dh);
            this.bus.register(dh);
        }
        this.parent = parent;
        this.parent.register(this.bus);
    }
    
    public void unregister() {
        this.parent.unregister(bus);
        this.parent = null;
    }
    
    public <T> T getDataHolder(Experience o) {
        Object dh = dataholders.get(o);
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
            Object o = k.createDataHolder();
            if (!AnnotationSerializer.canAnnotationSerialize(o)) {
                continue;
            }
            NBTCompound data = datacompound.getCompound(id);
            AnnotationSerializer.deserialize(o, data);
            dataholders.put(k, o);
            this.bus.register(o);
        }
        
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        NBTCompound datacompound = new NBTCompound();
        for (ObjectMap.Entry<Experience, Object> e : dataholders.entries()) {
            if (!AnnotationSerializer.canAnnotationSerialize(e.value)) {
                System.out.println(e);
                continue;
            }
            NBTCompound data = AnnotationSerializer.serialize(e.value);
            datacompound.putCompound(EXPERIENCE_REGISTRY.getId(e.key), data);
        }
        nbt.putCompound("data", datacompound);
    }
    
}
