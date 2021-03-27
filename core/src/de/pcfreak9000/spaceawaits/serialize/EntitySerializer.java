package de.pcfreak9000.spaceawaits.serialize;

import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;

public class EntitySerializer {
    
    private static final ComponentMapper<SerializeEntityComponent> seMapper = ComponentMapper
            .getFor(SerializeEntityComponent.class);
    
    //Check for illegal characters!!
    
    public static NBTCompound serializeEntity(Entity entity) {
        NBTCompound nbt = null;
        if (seMapper.has(entity)) {
            nbt = new NBTCompound();
            WorldEntityFactory fac = seMapper.get(entity).factory;
            GameRegistry.WORLD_ENTITY_REGISTRY.checkRegistered(fac);
            String facId = GameRegistry.WORLD_ENTITY_REGISTRY.getId(seMapper.get(entity).factory);
            nbt.putString("entityFactoryId", facId);
            ImmutableArray<Component> comps = entity.getComponents();
            for (Component c : comps) {
                if (c instanceof NBTSerializable) {
                    GameRegistry.WORLD_COMPONENT_REGISTRY.checkRegistered(c.getClass());
                    String id = GameRegistry.WORLD_COMPONENT_REGISTRY.getId(c.getClass());
                    NBTSerializable serializable = (NBTSerializable) c;
                    NBTTag tag = serializable.writeNBT();
                    nbt.put(id, tag);
                }
            }
        }
        return nbt;
    }
    
    public static Entity deserializeEntity(NBTCompound compound) {
        String entityFactoryId = compound.getString("entityFactoryId");
        if (!GameRegistry.WORLD_ENTITY_REGISTRY.isRegistered(entityFactoryId)) {
            return null;
        }
        //TODO What about components that are not added in there? Or if some of them have been removed? What about the physics stuff?
        Entity ent = GameRegistry.WORLD_ENTITY_REGISTRY.get(entityFactoryId).createEntity();//recreateEntity()?
        return deserializeEntity(ent, compound);
    }
    
    public static Entity deserializeEntity(Entity ent, NBTCompound comp) {
        if (seMapper.has(ent)) {
            for (Map.Entry<String, NBTTag> entry : comp) {
                if (entry.getKey().equals("entityFactoryId")) {
                    continue;
                }
                if (!GameRegistry.WORLD_COMPONENT_REGISTRY.isRegistered(entry.getKey())) {
                    //throw new IllegalStateException("Component not found: " + entry.getKey());
                    continue;//Ignore this case, maybe a mod has been removed, etc...
                }
                Class<? extends Component> cClass = GameRegistry.WORLD_COMPONENT_REGISTRY.get(entry.getKey());
                Component c = ent.getComponent(cClass);
                if (c instanceof NBTSerializable) {
                    NBTSerializable sc = (NBTSerializable) c;
                    sc.readNBT(entry.getValue());
                } else {
                    throw new IllegalStateException("Component is not serializable but was serialized");
                }
            }
            return ent;
        }
        throw new IllegalArgumentException("Entity is not serializable but was serialized");
    }
    
}
