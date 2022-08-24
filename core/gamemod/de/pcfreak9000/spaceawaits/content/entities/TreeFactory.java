package de.pcfreak9000.spaceawaits.content.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;

import de.pcfreak9000.spaceawaits.content.components.TreeStateComponent;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.item.ItemEntityFactory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
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
import de.pcfreak9000.spaceawaits.world.ecs.content.RandomTickComponent;
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
        pc.considerSensorsAsBlocking = true;
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
        bc.setRequired(Components.TRANSFORM);
        bc.entityBroken = (world, ent) -> {
            world.despawnEntity(ent);
            TransformComponent tcc = Components.TRANSFORM.get(entity);
            Entity e = ItemEntityFactory.setupItemEntity(new ItemStack(Tiles.WOOD.getItemDropped(), 1), tcc.position.x,
                    tcc.position.y);
            world.spawnEntity(e, false);
        };
        entity.add(bc);
        RandomTickComponent rtc = new RandomTickComponent();
        rtc.setRequired(Components.TRANSFORM);//Hmm
        rtc.tickable = (world) -> {
            TransformComponent tcc = Components.TRANSFORM.get(entity);
            //PhysicsComponent pcc = Components.PHYSICS.get(entity);
            float f0 = world.getWorldRandom().nextFloat();
            float f1 = world.getWorldRandom().nextFloat();
            Entity e = ItemEntityFactory.setupItemEntity(new ItemStack(Items.TWIG, 1), tcc.position.x + f0 * 1.5f,
                    tcc.position.y + 2 + f1 * 3);
            world.spawnEntity(e, false);
        };
        entity.add(rtc);
        OnNeighbourChangeComponent oncc = new OnNeighbourChangeComponent();
        oncc.setRequired(Components.PHYSICS, Components.TRANSFORM);
        oncc.onNeighbourTileChange = new OnNeighbourTileChange() {
            
            @Override
            public void onNeighbourTileChange(World world, TileSystem tileSystem, Entity entity, Tile newNeighbour,
                    Tile oldNeighbour, int ngtx, int ngty, TileLayer layer) {
                if (layer == TileLayer.Back) {
                    return;
                }
                TransformComponent tc = Components.TRANSFORM.get(entity);
                if (tc.position.y > ngty) {
                    for (int i = 0; i < 2; i++) {
                        if (tileSystem.getTile(Tile.toGlobalTile(tc.position.x) + i, ngty, layer).isSolid()) {
                            return;
                        }
                    }
                    entity.getComponent(TreeStateComponent.class).loose = true;
                    Body b = Components.PHYSICS.get(entity).body.getBody();
                    b.setType(BodyType.DynamicBody);
                    for (Fixture f : b.getFixtureList()) {
                        f.setSensor(false);
                    }
                }
            }
        };
        entity.add(oncc);
        return entity;
    }
    
}