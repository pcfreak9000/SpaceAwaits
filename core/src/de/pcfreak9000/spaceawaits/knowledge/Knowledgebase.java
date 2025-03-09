package de.pcfreak9000.spaceawaits.knowledge;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Knowledgebase implements INBTSerializable {

    public static final Registry<Knowledge> KNOWLEDGE_REGISTRY = new Registry<>();

    private ObjectSet<String> unlockedKnowledgeIds = new ObjectSet<>();

    // TODO this needs to be serialized as well
    private ObjectMap<Knowledge, Object> dataholders = new ObjectMap<>();

    public <T> T getDataHolder(Knowledge o) {
        if (!o.hasData()) {
            throw new IllegalArgumentException();
        }
        Object dh = dataholders.get(o);
        if (dh == null) {
            KNOWLEDGE_REGISTRY.checkRegistered(o);// checking on creation should be sufficient
            dh = o.createDataHolder();
            dataholders.put(o, dh);
        }
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
            dataholders.remove(obs);
            // TODO fire obseervation unlock event?
            // for now just print something:
            System.out.println("Congrats on unlocking the observation " + obs.getDisplayName());
        }
    }

    @Override
    public void readNBT(NBTCompound nbt) {
        NBTList sl = nbt.getList("unlocked");
        for (int i = 0; i < sl.size(); i++) {
            // maybe check registered...?
            unlockedKnowledgeIds.add(sl.getString(i));
        }
    }

    @Override
    public void writeNBT(NBTCompound nbt) {
        NBTList sl = new NBTList(NBTType.String);
        for (String s : unlockedKnowledgeIds) {
            sl.addString(s);
        }
        nbt.putList("unlocked", sl);
        // NBTCompound data = new NBTCompound();
    }

}
