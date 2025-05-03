package de.pcfreak9000.spaceawaits.flat;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.ecs.EntityFactory;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;

public class XXXXEntityFactory implements EntityFactory {
    public static Entity setupXXXXEntity(float x, float y) {
        Entity e = CoreRes.XXXX_FACTORY.createEntity();
        Components.TRANSFORM.get(e).position.set(x, y);
        return e;
    }
    
    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.add(new ChunkComponent());
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(RenderLayers.ENTITY));
        
        e.add(new TransformComponent());
        PhysicsComponent pc = new PhysicsComponent();
        pc.gurke = true;
        pc.factory = new XXXXBodyFactory();//AABBBodyFactory.builder().dimensions(Item.WORLD_SIZE, Item.WORLD_SIZE).create();
        e.add(pc);
        return e;
    }
}
