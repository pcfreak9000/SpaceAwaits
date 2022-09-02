package de.pcfreak9000.spaceawaits.world.ecs.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Component;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsStats")
public class StatsComponent implements INBTSerializable, Component {
    
    public static final class StatData {
        public float current;
        public float max;
        
        public StatData(float current, float max) {
            this.current = current;
            this.max = max;
        }
        
        public StatData() {
        }
        
        public void add(float v) {
            current += v;
            current = Mathf.clamp(current, 0, max);
        }
        
        public boolean isMin() {
            return current <= 0f;
        }
        
        public boolean isMax() {
            return current >= max;
        }
    }
    
    public final Map<String, StatData> statDatas = new HashMap<>();
    
    public StatData get(String id) {
        return statDatas.get(id);
    }
    
    public void put(String id, StatData statData) {
        this.statDatas.put(id, statData);
    }
    
    public boolean has(String id) {
        return this.statDatas.containsKey(id);
    }
    
    @Override
    public void readNBT(NBTCompound c) {
        for (Entry<String, NBTTag> e : c.entrySet()) {
            String k = e.getKey();
            String s = e.getKey().substring(0, e.getKey().length() - 1);
            StatData statData = statDatas.get(s);
            if (statData == null) {
                statData = new StatData();
                statDatas.put(s, statData);
            }
            if (k.endsWith("c")) {
                statData.current = c.getFloat(k);
            }
            if (k.endsWith("m")) {
                statData.max = c.getFloat(k);
            }
        }
    }
    
    @Override
    public void writeNBT(NBTCompound c) {
        for (Entry<String, StatData> e : statDatas.entrySet()) {
            c.putFloat(e.getKey() + "c", e.getValue().current);
            c.putFloat(e.getKey() + "m", e.getValue().max);
        }
    }
    
}
