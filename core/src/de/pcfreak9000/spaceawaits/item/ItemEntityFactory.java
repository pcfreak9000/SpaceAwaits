package de.pcfreak9000.spaceawaits.item;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.physics.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderComponent;

public class ItemEntityFactory implements WorldEntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity();
        e.add(new ItemStackComponent());
        e.add(new ChunkMarkerComponent());
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(1, "item"));
        e.add(new TransformComponent());
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = AABBBodyFactory.builder().dimensions(Item.WORLD_SIZE, Item.WORLD_SIZE).create();//new AABBBodyFactory(200, 100);
        e.add(pc);
        return e;
    }
}
