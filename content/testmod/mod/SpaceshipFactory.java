package mod;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.comp.CompositeInventory;
import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.Activator;
import de.pcfreak9000.spaceawaits.world.ecs.content.ActivatorComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.RenderStatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.StatsComponent.StatData;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class SpaceshipFactory implements WorldEntityFactory {
    
    private static final TextureProvider tex = TextureProvider.get("spaceship2_final.png");
    
    @Override
    public Entity createEntity() {
        Entity entity = new EntityImproved();
        entity.flags = 1029384756;
        entity.add(new ChunkComponent());
        RenderRenderableComponent rec = new RenderRenderableComponent();
        rec.renderable = tex;
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
        entity.add(new RenderComponent(RenderLayers.ENTITY));
        ActivatorComponent ac = new ActivatorComponent();
        ac.layer = RenderLayers.ENTITY;
        ac.activators.add(tt);
        entity.add(ac);
        CompositeInventoryComponent cic = new CompositeInventoryComponent();
        cic.compositeInv = new CompositeInventory();
        entity.add(cic);
        DisassemblerComponent disscomp = new DisassemblerComponent();
        disscomp.disassembler = new Disassembler(0);
        entity.add(disscomp);
        ComponentInventoryShip invshipcomp = new ComponentInventoryShip();
        entity.add(invshipcomp);
        StatsComponent sc = new StatsComponent();
        StatData sd = new StatData(1000, 1000);
        sc.put("mechHealth", sd);
        entity.add(sc);
        RenderStatsComponent rsc = new RenderStatsComponent();
        rsc.width = rec.width / 1.5f;
        rsc.xOff = rec.width * ((1 - 1 / 1.5f) * 0.5f);
        rsc.yOff = rec.height + 0.1f;
        entity.add(rsc);
        return entity;
    }
    
    private Activator tt = new Activator() {
        
        @Override
        public boolean handle(float mousex, float mousey, Entity entity, World world, Entity source) {
            Player player = source.getComponent(PlayerInputComponent.class).player;
            if (!Components.STATS.get(entity).get("mechHealth").isMax()) {
                player.openContainer(
                        new ContainerInventoryShip(entity.getComponent(ComponentInventoryShip.class).invShip));
                return true;
            }
            player.openContainer(new ContainerShipSelect(entity));
            return true;
        }
        
        @Override
        public boolean isContinuous() {
            return false;
        }
        
        @Override
        public Object getInputKey() {
            return EnumInputIds.Use;
        }
    };
}
