import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.gui.ContainerDisassembler;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.Activator;
import de.pcfreak9000.spaceawaits.world.ecs.content.ActivatorComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

public class SpaceshipFactory implements WorldEntityFactory {
    
    private static final TextureProvider tex = TextureProvider.get("spaceship2_final.png");
    
    @Override
    public Entity createEntity() {
        Entity entity = new EntityImproved();
        entity.add(new ChunkMarkerComponent());
        RenderTextureComponent rec = new RenderTextureComponent();
        rec.texture = tex;
        rec.color = Color.WHITE;
        rec.width = 159 / 32f;
        rec.height = 73 / 32f;
        entity.add(rec);
        TransformComponent tc = new TransformComponent();
        entity.add(tc);
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = new SpaceshipBodyFactory();//AABBBodyFactory.builder().dimensions(rec.width, rec.height).create();//new AABBBodyFactory(200, 100);
        entity.add(pc);
        entity.add(new SerializeEntityComponent(this));
        entity.add(new RenderComponent(RenderLayers.ENTITY, "entity"));
        ActivatorComponent ac = new ActivatorComponent();
        ac.layer = RenderLayers.ENTITY;
        ac.activators.add(tt);
        entity.add(ac);
        return entity;
    }
    
    private Activator tt = new Activator() {
        
        @Override
        public boolean handle(float mousex, float mousey, Entity entity, World world, Entity source) {
            Player player = source.getComponent(PlayerInputComponent.class).player;
            player.openContainer(new ContainerDisassembler());
            System.out.println("Gurke");
            return true;
        }
        
        @Override
        public Object getInputKey() {
            return EnumInputIds.Use;
        }
    };
}
