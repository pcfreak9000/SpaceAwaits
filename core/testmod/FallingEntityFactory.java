import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.physics.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.TextureSpriteAction;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderEntityComponent;

public class FallingEntityFactory implements WorldEntityFactory {
    @Override
    public Entity createEntity() {
        Entity entity = new EntityImproved();
        entity.flags = 3;
        entity.add(new ChunkMarkerComponent());
        RenderEntityComponent rec = new RenderEntityComponent();
        Sprite s = new Sprite();
        s.setSize(200 / 16, 100 / 16);
        rec.sprite = s;
        rec.action = new TextureSpriteAction(DMod.instance.texture);
        entity.add(rec);
        TransformComponent tc = new TransformComponent();
        entity.add(tc);
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = AABBBodyFactory.builder().dimensions(200 / 16, 100 / 16).create();//new AABBBodyFactory(200, 100);
        entity.add(pc);
        entity.add(new SerializeEntityComponent(this));
        entity.add(new RenderComponent(1, "entity"));
        return entity;
    }
}
