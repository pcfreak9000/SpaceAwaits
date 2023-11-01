package de.pcfreak9000.spaceawaits.util;

import java.util.Objects;
import java.util.function.Consumer;

import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongQueue;

public class SpecialCache2D<V> {
    private int max;
    private int reducedMax;
    private LongQueue keyUsagePrioQueue = new LongQueue();
    private LongMap<V> cache = new LongMap<>();
    
    private Int2DFunction<V> freshSupply;
    private Consumer<V> dump;
    
    public boolean autocheckSize = true;
    
    public static interface Int2DFunction<V> {
        V apply(int x, int y);
    }
    
    public SpecialCache2D(int max, int reducedMax, Int2DFunction<V> freshsupply, Consumer<V> dump) {
        if (reducedMax > max) {
            throw new IllegalArgumentException("reducedMax > max");
        }
        this.max = max;
        this.reducedMax = reducedMax;
        this.freshSupply = freshsupply;
        this.dump = dump;
    }
    
    public void checkCacheSize() {
        if (keyUsagePrioQueue.size > max) {
            while (keyUsagePrioQueue.size > reducedMax) {
                long k = keyUsagePrioQueue.removeFirst();
                V removed = cache.remove(k);
                if (dump != null) {
                    dump.accept(removed);
                }
            }
        }
    }
    
    private void checkCacheSizeInt() {
        if (autocheckSize) {
            checkCacheSize();
        }
    }
    
    public int size() {
        return cache.size;
    }
    
    public boolean hasKey(int x, int y) {
        return cache.containsKey(IntCoords.toLong(x, y));
    }
    
    public V getFromCache(int x, int y) {
        long key = IntCoords.toLong(x, y);
        return getFromCacheInternal(key);
    }
    
    private V getFromCacheInternal(long key) {
        V v = cache.get(key);
        if (v != null && !Objects.equals(key, keyUsagePrioQueue.last())) {
            keyUsagePrioQueue.removeValue(key); //<- TODO this could potentially decrease the performance....
            keyUsagePrioQueue.addLast(key);
            checkCacheSizeInt();
        }
        return v;
    }
    
    public V getOrFresh(int x, int y) {
        long key = IntCoords.toLong(x, y);
        V v = getFromCacheInternal(key);
        if (v == null && freshSupply != null) {
            v = freshSupply.apply(x, y);
            cache.put(key, v);
            keyUsagePrioQueue.addLast(key);
            checkCacheSizeInt();
        }
        return v;
    }
    
    /**
     * put into cache. existing element is replaced and dumped
     * @param x
     * @param y
     * @param v
     */
    public void put(int x, int y, V v) {
        long key = IntCoords.toLong(x, y);
        remove(x, y);
        cache.put(key, v);
        keyUsagePrioQueue.addLast(key);
        checkCacheSizeInt();
    }
    
    /**
     * remove from cache, but dont dump
     * @param x
     * @param y
     * @return
     */
    public V take(int x, int y) {
        long key = IntCoords.toLong(x, y);
        V v = cache.remove(key);
        if (v != null) {
            keyUsagePrioQueue.removeValue(key);
        }
        return v;
    }
    
    /**
     * remove from cache and dump
     * @param x
     * @param y
     */
    public void remove(int x, int y) {
        V v = take(x, y);
        if (v != null && dump != null) {
            dump.accept(v);
        }
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
    
    //values() is not immutable :/
    public LongMap.Values<V> values() {
        return cache.values();
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
