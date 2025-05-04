package de.pcfreak9000.spaceawaits.knowledge;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import de.omnikryptec.event.Event;
import de.omnikryptec.event.EventBus;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.AnnotationSerializer;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Knowledgebase implements INBTSerializable {
    
    public static class KnowledgeUnlockedEvent extends Event {
        public final Knowledge knowledge;
        
        public KnowledgeUnlockedEvent(Knowledge knowledge) {
            this.knowledge = knowledge;
        }
        
    }
    
    public static final Registry<Knowledge> KNOWLEDGE_REGISTRY = new Registry<>();
    
    private ObjectSet<String> unlockedKnowledgeIds = new ObjectSet<>();
    
    private ObjectMap<Knowledge, UnlockProgress> dataholders = new ObjectMap<>();
    
    private EventBus bus = new EventBus();
    private EventBus parent;
    
    public void register(EventBus parent) {
        for (Knowledge k : KNOWLEDGE_REGISTRY.getAll()) {
            if (!k.hasData()) {
                continue;
            }
            if (dataholders.containsKey(k)) {
                continue;
            }
            String id = KNOWLEDGE_REGISTRY.getId(k);
            if (unlockedKnowledgeIds.contains(id)) {
                continue;
            }
            UnlockProgress dh = k.createDataHolder(this);
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
    
    public <T extends UnlockProgress> T getUnlockProgress(Knowledge o) {
        if (!o.hasData()) {
            throw new IllegalArgumentException();
        }
        UnlockProgress dh = dataholders.get(o);
        return (T) dh;
    }
    
    public boolean isUnlocked(Knowledge obs) {
        return isUnlocked(KNOWLEDGE_REGISTRY.getId(obs));
    }
    
    public boolean isUnlocked(String key) {
        return unlockedKnowledgeIds.contains(key);
    }
    
    public void unlock(Knowledge obs) {
        KNOWLEDGE_REGISTRY.checkRegistered(obs);
        if (unlockedKnowledgeIds.add(KNOWLEDGE_REGISTRY.getId(obs))) {
            UnlockProgress up = dataholders.remove(obs);
            bus.unregister(up);
            SpaceAwaits.BUS.post(new KnowledgeUnlockedEvent(obs));
        }
    }
    
    @Override
    public void readNBT(NBTCompound nbt) {
        NBTList sl = nbt.getList("unlocked");
        for (int i = 0; i < sl.size(); i++) {
            // maybe check registered...?
            unlockedKnowledgeIds.add(sl.getString(i));
        }
        NBTCompound datacompound = nbt.getCompound("data");
        for (String id : datacompound.keySet()) {
            if (!KNOWLEDGE_REGISTRY.isRegistered(id)) {
                continue;
            }
            Knowledge k = KNOWLEDGE_REGISTRY.get(id);
            if (!k.hasData()) {
                continue;
            }
            UnlockProgress o = k.createDataHolder(this);
            if (!AnnotationSerializer.canAnnotationSerialize(o)) {
                continue;
            }
            NBTCompound data = datacompound.getCompound(id);
            AnnotationSerializer.deserialize(o, data);
            dataholders.put(k, o);
            bus.register(o);
        }
        
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        NBTList sl = new NBTList(NBTType.String);
        for (String s : unlockedKnowledgeIds) {
            sl.addString(s);
        }
        nbt.putList("unlocked", sl);
        NBTCompound datacompound = new NBTCompound();
        for (ObjectMap.Entry<Knowledge, UnlockProgress> e : dataholders.entries()) {
            if (!AnnotationSerializer.canAnnotationSerialize(e.value)) {
                continue;
            }
            NBTCompound data = AnnotationSerializer.serialize(e.value);
            datacompound.putCompound(KNOWLEDGE_REGISTRY.getId(e.key), data);
        }
        nbt.putCompound("data", datacompound);
    }
    
}
