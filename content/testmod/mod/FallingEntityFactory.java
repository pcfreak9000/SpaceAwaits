package mod;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class FallingEntityFactory implements WorldEntityFactory {
    @Override
    public Entity createEntity() {
        Entity entity = new EntityImproved();
        entity.flags = 3;
        entity.add(new ChunkComponent());
        RenderRenderableComponent rec = new RenderRenderableComponent();
        Sprite s = new Sprite();
        s.setSize(200 / 16, 100 / 16);
        //rec.sprite = s;
        //rec.action = new TextureSpriteAction(DMod.instance.texture);
        entity.add(rec);
        TransformComponent tc = new TransformComponent();
        entity.add(tc);
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = AABBBodyFactory.builder().dimensions(200 / 16, 100 / 16).create();//new AABBBodyFactory(200, 100);
        entity.add(pc);
        entity.add(new SerializeEntityComponent(this));
        entity.add(new RenderComponent(RenderLayers.ENTITY));
        return entity;
    }
}
