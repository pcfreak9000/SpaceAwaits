package de.pcfreak9000.spaceawaits.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.badlogic.gdx.utils.Queue;

public class SpecialCache<K, V> {
    private int max;
    private int reducedMax;
    
    private Queue<K> keyUsagePrioQueue = new Queue<>();
    private Map<K, V> cache = new HashMap<>();
    
    private Function<K, V> freshSupply;
    private Consumer<V> dump;
    
    private final Collection<V> unmodValues;
    
    public SpecialCache(int max, int reducedMax, Function<K, V> freshsupply, Consumer<V> dump) {
        if (reducedMax > max) {
            throw new IllegalArgumentException("reducedMax > max");
        }
        this.max = max;
        this.reducedMax = reducedMax;
        this.freshSupply = freshsupply;
        this.dump = dump;
        this.unmodValues = Collections.unmodifiableCollection(this.cache.values());
    }
    
    public void checkCacheSize() {
        if (keyUsagePrioQueue.size > max) {
            while (keyUsagePrioQueue.size > reducedMax) {
                K k = keyUsagePrioQueue.removeFirst();
                V removed = cache.remove(k);
                if (dump != null) {
                    dump.accept(removed);
                }
            }
        }
    }
    
    public int size() {
        return cache.size();
    }
    
    public boolean hasKey(K key) {
        return cache.containsKey(key);
    }
    
    public V getFromCache(K key) {
        V v = cache.get(key);
        return v;
    }
    
    public V getOrFresh(K key) {
        V v = getFromCache(key);
        if (v == null) {
            v = freshSupply.apply(key);
            cache.put(key, v);
            keyUsagePrioQueue.addLast(key);
            checkCacheSize();
        } else if (!Objects.equals(key, keyUsagePrioQueue.last())) {
            keyUsagePrioQueue.removeValue(key, false); //<- TODO this could potentially decrease the performance....
            keyUsagePrioQueue.addLast(key);
            checkCacheSize();
        }
        return v;
    }
    
    //clear/dump all
    public void clear() {
        if (dump != null) {
            for (V v : cache.values()) {
                dump.accept(v);
            }
        }
        cache.clear();
        keyUsagePrioQueue.clear();
    }
    
    //values -> immutable
    public Collection<V> values() {
        return unmodValues;
    }
    
    //remove
    public V remove(K key) {
        V v = cache.get(key);
        keyUsagePrioQueue.removeValue(key, false);
        return v;
    }
    
    //setmax/setreducedmax
    //automatically checks cache size
    public void setMaxs(int max, int maxr) {
        setMaxReduced(maxr);
        setMax(max);
    }
    
    public void setMaxReduced(int maxr) {
        this.reducedMax = maxr;
    }
    
    //automatically checks cache size
    public void setMax(int max) {
        this.max = max;
        checkCacheSize();
    }
    
}
