package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.physics.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;

public class ItemEntityFactory implements WorldEntityFactory {
    private static final ComponentMapper<TransformComponent> TRANSFORM_COMP_MAPPER = ComponentMapper
            .getFor(TransformComponent.class);
    private static final ComponentMapper<ItemStackComponent> ITEMSTACK_COMP_MAPPER = ComponentMapper
            .getFor(ItemStackComponent.class);
    
    public static Entity setupItemEntity(ItemStack stack, float x, float y) {
        Entity e = CoreRes.ITEM_FACTORY.createEntity();
        ITEMSTACK_COMP_MAPPER.get(e).stack = stack;
        TRANSFORM_COMP_MAPPER.get(e).position.set(x, y);
        return e;
    }
    
    private ItemContactListener listener = new ItemContactListener();
    
    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.add(new ItemStackComponent());
        e.add(new ChunkMarkerComponent());
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(0.05f, "item"));
        e.add(new TransformComponent());
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = new ItemBodyFactory();//AABBBodyFactory.builder().dimensions(Item.WORLD_SIZE, Item.WORLD_SIZE).create();
        e.add(new ContactListenerComponent(listener));
        e.add(pc);
        return e;
    }
}
