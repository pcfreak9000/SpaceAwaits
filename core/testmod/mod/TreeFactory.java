package mod;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;

import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.ActivatorComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.BreakableComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.OnNeighbourChangeComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.OnNeighbourChangeComponent.OnNeighbourTileChange;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

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
        
        pc.factory = new TreeBodyFactory();
        entity.add(new TreeStateComponent());
        entity.add(pc);
        entity.add(new SerializeEntityComponent(this));
        entity.add(new RenderComponent(RenderLayers.ENTITY));
        ActivatorComponent ac = new ActivatorComponent();
        ac.layer = RenderLayers.ENTITY;
        //ac.activators.add(tt);
        entity.add(ac);
        BreakableComponent bc = new BreakableComponent();
        bc.entityBroken = (world, ent) -> {
            world.despawnEntity(ent);
        };
        entity.add(bc);
        OnNeighbourChangeComponent oncc = new OnNeighbourChangeComponent();
        oncc.onNeighbourTileChange = new OnNeighbourTileChange() {
            
            @Override
            public void onNeighbourTileChange(World world, TileSystem tileSystem, Entity entity, Tile newNeighbour,
                    Tile oldNeighbour, int ngtx, int ngty, TileLayer layer) {
                TransformComponent tc = Components.TRANSFORM.get(entity);
                if (!newNeighbour.isSolid()) {
                    if (tc.position.y > ngty) {
                        entity.getComponent(TreeStateComponent.class).loose = true;
                        Body b = Components.PHYSICS.get(entity).body.getBody();
                        b.setType(BodyType.DynamicBody);
                        for (Fixture f : b.getFixtureList()) {
                            f.setSensor(false);
                        }
                    }
                }
            }
        };
        entity.add(oncc);
        return entity;
    }
    
}
