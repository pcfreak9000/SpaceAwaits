package de.pcfreak9000.spaceawaits.registry;

import java.util.Collection;
import java.util.HashMap;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.comp.CompositeManager;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.tile.IRegen;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

/**
 * used to register everything annotated by {@link RegisterSensitive}
 *
 * @author pcfreak9000
 *
 * @param <T> the Type of whats being registered
 */
public class Registry<T> {
    
    public static final Registry<Tile> TILE_REGISTRY = new Registry<Tile>() {
        @Override
        public Registry<Tile> register(String name, Tile data) {
            super.register(name, data);
            Item item = data.getRegisterItem();
            if (item != null) {
                ITEM_REGISTRY.register(name, item);
            }
            return this;
        };
    };
    public static final Registry<Item> ITEM_REGISTRY = new Registry<>();
    
    public static final Registry<IRegen<?>> REGEN_REGISTRY = new Registry<>();
    
    public static final CompositeManager COMPOSITE_MANAGER = new CompositeManager();
    
    public static final Registry<WorldEntityFactory> WORLD_ENTITY_REGISTRY = new Registry<>();
    
    public static final GeneratorRegistry GENERATOR_REGISTRY = new GeneratorRegistry();
    
    //public static final Registry<IGeneratingLayer<?, ?>> GENERATOR_REGISTRY = new Registry<>();
    
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    protected final HashMap<String, T> registered = new HashMap<>();
    protected final HashMap<T, String> registeredReverse = new HashMap<>();
    
    //    public final boolean allowOverride;
    //    
    //    public GameRegistry() {
    //        this(true);
    //    }
    //    
    //    public GameRegistry(boolean allowOverride) {
    //        this.allowOverride = allowOverride;
    //    }
    
    public Registry<T> register(final String name, final T data) {
        final T before = this.registered.put(name, data);
        this.registeredReverse.put(data, name);
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
    
    public T getOrDefault(String name, T def) {
        T t = this.registered.get(name);
        if (t == null) {
            return def;
        }
        return t;
    }
    
    public boolean isRegistered(final String name) {
        return this.registered.containsKey(name);
    }
    
    public boolean isRegistered(final T data) {
        return this.registered.containsValue(data);
    }
    
    public String getId(T data) {
        return this.registeredReverse.get(data);
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
