import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.MovingWorldEntityComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.RenderEntityComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.TextureSpriteAction;

public class FallingEntityFactory implements WorldEntityFactory {
    @Override
    public Entity createEntity() {
        Entity entity = new Entity();
        entity.add(new MovingWorldEntityComponent());
        RenderEntityComponent rec = new RenderEntityComponent();
        Sprite s = new Sprite();
        s.setSize(200, 100);
        rec.sprite = s;
        rec.action = new TextureSpriteAction(DMod.instance.texture);
        entity.add(rec);
        TransformComponent tc = new TransformComponent();
        entity.add(tc);
        PhysicsComponent pc = new PhysicsComponent();
        pc.acceleration.set(0, -98.1f);
        pc.w = 200;
        pc.h = 100;
        entity.add(pc);
        return entity;
    }
}
