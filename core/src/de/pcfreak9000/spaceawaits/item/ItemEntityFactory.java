package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.Entity;

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
    
    private ItemContactListener listener = new ItemContactListener();
    
    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.add(new ItemStackComponent());
        e.add(new ChunkMarkerComponent());
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(1, "item"));
        e.add(new TransformComponent());
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = new ItemBodyFactory();//AABBBodyFactory.builder().dimensions(Item.WORLD_SIZE, Item.WORLD_SIZE).create();
        e.add(new ContactListenerComponent(listener));
        e.add(pc);
        return e;
    }
}
