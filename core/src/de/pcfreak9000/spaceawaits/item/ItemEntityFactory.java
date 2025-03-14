package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.ecs.EntityFactory;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;

public class ItemEntityFactory implements EntityFactory {
    
    public static Entity setupItemEntity(ItemStack stack, float x, float y) {
        Entity e = CoreRes.ITEM_FACTORY.createEntity();
        Components.ITEM_STACK.get(e).stack = stack;
        Components.TRANSFORM.get(e).position.set(x, y);
        return e;
    }
    
    private ItemContactListener listener = new ItemContactListener();
    
    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.add(new ItemStackComponent());
        e.add(new ChunkComponent());
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(RenderLayers.ENTITY));
        e.add(new TransformComponent());
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = new ItemBodyFactory();//AABBBodyFactory.builder().dimensions(Item.WORLD_SIZE, Item.WORLD_SIZE).create();
        e.add(new ContactListenerComponent(listener));
        e.add(pc);
        return e;
    }
}
