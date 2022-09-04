package de.pcfreak9000.spaceawaits.registry;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class WorldComponentRegistry extends Registry<Class<? extends Component>> {
    @Override
    public Registry<Class<? extends Component>> register(String name, Class<? extends Component> data) {
        if (NBTSerializable.class.isAssignableFrom(data)) {
            try {
                data.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                        "Component implements NBTSerializable but does not have a nullary constructor");
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return super.register(name, data);
    }
}
