package mod;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.ActivatorComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

public class TreeFactory implements WorldEntityFactory {
    private static final TextureProvider tex = TextureProvider.get("baum.png");
    
    @Override
    public Entity createEntity() {
        Entity entity = new EntityImproved();
        entity.add(new ChunkMarkerComponent());
        RenderTextureComponent rec = new RenderTextureComponent();
        rec.texture = tex;
        rec.color = Color.WHITE;
        
        rec.width = 50 / 32f;
        rec.height = 222 / 32f;
        
        entity.add(rec);
        TransformComponent tc = new TransformComponent();
        entity.add(tc);
        PhysicsComponent pc = new PhysicsComponent();
        
        pc.factory = new SpaceshipBodyFactory();
        
        //entity.add(pc);
        entity.add(new SerializeEntityComponent(this));
        entity.add(new RenderComponent(RenderLayers.ENTITY));
        ActivatorComponent ac = new ActivatorComponent();
        ac.layer = RenderLayers.ENTITY;
        //ac.activators.add(tt);
        entity.add(ac);
        return entity;
    }
    
}
