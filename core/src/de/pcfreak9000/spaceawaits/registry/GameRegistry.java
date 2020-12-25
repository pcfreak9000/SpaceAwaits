package de.pcfreak9000.spaceawaits.registry;

import java.util.Collection;
import java.util.HashMap;

import de.omnikryptec.util.Logger;

/**
 * used to register everything annotated by {@link RegisterSensitive}
 *
 * @author pcfreak9000
 *
 * @param <T> the Type of whats being registered
 */
public class GameRegistry<T> {
    
    public static final TileRegistry TILE_REGISTRY = new TileRegistry();
    
    public static final GeneratorRegistry GENERATOR_REGISTRY = new GeneratorRegistry();
    
    public static final BackgroundRegistry BACKGROUND_REGISTRY = new BackgroundRegistry();
    
    public static final ItemRegistry ITEM_REGISTRY = new ItemRegistry();
    
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    protected final HashMap<String, T> registered = new HashMap<>();
    
    public GameRegistry<T> register(final String name, final T data) {
        final T before = this.registered.put(name, data);
        if (before != null) {
            this.LOGGER.info("Overriding: " + name);
        }
        return this;
    }
    
    public T get(final String name) {
        T t = this.registered.get(name);
        if (t == null) {
            throw new NullPointerException(String.format("no registry entry with name %s", name));
        }
        return this.registered.get(name);
    }
    
    public boolean isRegistered(final String name) {
        return this.registered.containsKey(name);
    }
    
    public boolean isRegistered(final T data) {
        return this.registered.containsValue(data);
    }
    
    public void checkRegistered(final T data) {
        if (!isRegistered(data)) {
            throw new IllegalStateException(data.getClass().getSimpleName() + " " + data + " is not registered!");
        }
    }
    
    public Collection<T> getAll() {
        return this.registered.values();
    }
    
}
