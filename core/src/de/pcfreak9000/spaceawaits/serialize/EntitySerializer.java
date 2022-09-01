package de.pcfreak9000.spaceawaits.serialize;

import java.lang.reflect.InvocationTargetException;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;

public class EntitySerializer {
    
    private static final EntityComponentHolderWrapper ENTITY_WRAPPER = new EntityComponentHolderWrapper();
    
    //TODO Check for illegal characters!! also in the registries...
    
    public static boolean isSerializable(Entity e) {
        return Components.SERIALIZE_ENTITY.has(e);
    }
    
    public static NBTCompound serializeEntity(Entity entityImproved) {
        if (isSerializable(entityImproved)) {
            NBTCompound nbt = new NBTCompound();
            WorldEntityFactory fac = Components.SERIALIZE_ENTITY.get(entityImproved).factory;
            GameRegistry.WORLD_ENTITY_REGISTRY.checkRegistered(fac);
            String facId = GameRegistry.WORLD_ENTITY_REGISTRY
                    .getId(Components.SERIALIZE_ENTITY.get(entityImproved).factory);
            nbt.putString("entityFactoryId", facId);
            //This could also happen in a serialize function in the factory
            nbt.putCompound("components", serializeEntityComponents(entityImproved));
            return nbt;
        }
        throw new RuntimeException("Entity is not serializable");
    }
    
    public static Entity deserializeEntity(NBTCompound compound) {
        String entityFactoryId = compound.getString("entityFactoryId");
        if (!GameRegistry.WORLD_ENTITY_REGISTRY.isRegistered(entityFactoryId)) {
            return null;
        }
        Entity ent = GameRegistry.WORLD_ENTITY_REGISTRY.get(entityFactoryId).recreateEntity();
        if (!isSerializable(ent)) {
            throw new IllegalArgumentException("Entity is not serializable but was serialized");//Hmm, throw or not throw?
        }
        //This could also happen in a deserialize function in the factory?
        deserializeEntityComponents(ent, compound.getCompound("components"));
        return ent;
    }
    
    public static NBTCompound serializeEntityComponents(Entity entityImproved) {
        NBTCompound comp = new NBTCompound();
        serializeComponents(entityImproved.getComponents(), comp);
        return comp;
    }
    
    public static void deserializeEntityComponents(Entity existing, NBTCompound componentCompound) {
        ENTITY_WRAPPER.entity = existing;
        deserializeComponents(ENTITY_WRAPPER, componentCompound);
    }
    
//    public static void serializeComponents(Iterable<Component> components, NBTCompound nbt) {
//        for (Component c : components) {
//            if (c instanceof NBTSerializable) {
//                GameRegistry.WORLD_COMPONENT_REGISTRY.checkRegistered(c.getClass());
//                String id = GameRegistry.WORLD_COMPONENT_REGISTRY.getId(c.getClass());
//                NBTSerializable serializable = (NBTSerializable) c;
//                NBTTag tag = serializable.writeNBT();
//                nbt.put(id, tag);
//            }
//        }
//    }
    
    public static void serializeComponents(Iterable<Component> components, NBTCompound comp) {
        for (Component c : components) {
            if (AnnotationSerializer.canAnnotationSerialize(c)) {
                AnnotationSerializer.serialize(comp, c);
            }
        }
    }
    
    public static void deserializeComponents(ComponentHolder ent, NBTCompound comp) {
        for (String s : comp.keySet()) {
            Class<? extends Component> cl = SpaceAwaits.getSpaceAwaits().getComponentByKey(s);
            //Component is serializable but doesnt exist on the holder
            if (cl != null && ent.getComponent(cl) == null) {
                try {
                    Component c = cl.getDeclaredConstructor().newInstance();
                    ent.add(c);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Component c : ent.getComponents()) {
            if (AnnotationSerializer.canAnnotationSerialize(c)) {
                AnnotationSerializer.deserialize(comp, c);
            }
        }
    }
    
//    public static void deserializeComponents(ComponentHolder ent, NBTCompound comp) {
//        for (Map.Entry<String, NBTTag> entry : comp.entrySet()) {
//            if (!GameRegistry.WORLD_COMPONENT_REGISTRY.isRegistered(entry.getKey())) {
//                continue;//Ignore this case, maybe a mod has been removed, etc...
//            }
//            Class<? extends Component> cClass = GameRegistry.WORLD_COMPONENT_REGISTRY.get(entry.getKey());
//            Component c = ent.getComponent(cClass);
//            if (c == null) {
//                try {
//                    c = cClass.getDeclaredConstructor().newInstance();
//                    ent.add(c);
//                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
//                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (c instanceof NBTSerializable) {
//                NBTSerializable sc = (NBTSerializable) c;
//                sc.readNBT(entry.getValue());
//            } else {
//                throw new IllegalStateException("Component is not serializable but was serialized");
//            }
//        }
//    }
    
    private static final class EntityComponentHolderWrapper implements ComponentHolder {
        
        private Entity entity;
        
        @Override
        public void add(Component comp) {
            entity.add(comp);
        }
        
        @Override
        public Component getComponent(Class<? extends Component> clazz) {
            return entity.getComponent(clazz);
        }
        
        @Override
        public ImmutableArray<Component> getComponents() {
            return entity.getComponents();
        }
    }
}
