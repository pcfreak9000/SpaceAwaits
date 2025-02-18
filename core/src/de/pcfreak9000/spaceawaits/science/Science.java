package de.pcfreak9000.spaceawaits.science;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Science implements INBTSerializable {
    
    public static final Registry<Observation> OBSERVATION_REGISTRY = new Registry<>();
    
    private ObjectSet<String> unlockedObservationIds = new ObjectSet<>();
    
    //TODO this needs to be serialized as well
    private ObjectMap<Observation, Object> dataholders = new ObjectMap<>();
    
    public <T> T getDataHolder(Observation o) {
        Object dh = dataholders.get(o);
        if (dh == null) {
            OBSERVATION_REGISTRY.checkRegistered(o);//checking on creation should be sufficient
            dh = o.createDataHolder();
            dataholders.put(o, dh);
        }
        return (T) dh;
    }
    
    public boolean isUnlocked(Observation obs) {
        return isUnlocked(OBSERVATION_REGISTRY.getId(obs));
    }
    
    public boolean isUnlocked(String key) {
        return unlockedObservationIds.contains(key);
    }
    
    public void unlock(Observation obs) {
        if (unlockedObservationIds.add(OBSERVATION_REGISTRY.getId(obs))) {
            dataholders.remove(obs);
            //TODO fire obseervation unlock event?
            //for now just print something:
            System.out.println("Congrats on unlocking the observation " + obs.getDisplayName());
        }
    }
    
    @Override
    public void readNBT(NBTCompound nbt) {
        NBTList sl = nbt.getList("unlocked");
        for (int i = 0; i < sl.size(); i++) {
            //maybe check registered...?
            unlockedObservationIds.add(sl.getString(i));
        }
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        NBTList sl = new NBTList(NBTType.String);
        for (String s : unlockedObservationIds) {
            sl.addString(s);
        }
        nbt.putList("unlocked", sl);
        //NBTCompound data = new NBTCompound();
    }
    
}
