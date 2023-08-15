package de.pcfreak9000.spaceawaits.content.entities;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.content.components.Components;
import de.pcfreak9000.spaceawaits.content.components.TreeStateComponent;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.core.assets.TextureProvider;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.IBreakableEntity;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.ActivatorComponent;
import de.pcfreak9000.spaceawaits.world.ecs.BreakableComponent;
import de.pcfreak9000.spaceawaits.world.ecs.OnNeighbourChangeComponent;
import de.pcfreak9000.spaceawaits.world.ecs.RandomTickComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.OnNeighbourChangeComponent.OnNeighbourTileChange;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TreeFactory implements WorldEntityFactory {
    
    private static final TextureProvider tex = TextureProvider.get("baum.png");
    
    @Override
    public Entity createEntity() {
        Entity entity = new EntityImproved();
        entity.add(new ChunkComponent());
        RenderRenderableComponent rec = new RenderRenderableComponent();
        rec.renderable = tex;
        rec.color = Color.WHITE;
        
        rec.width = 50 / 16f;
        rec.height = 222 / 16f;
        
        entity.add(rec);
        TransformComponent tc = new TransformComponent();
        entity.add(tc);
        PhysicsComponent pc = new PhysicsComponent();
        pc.considerSensorsAsBlocking = true;
        pc.factory = new TreeBodyFactory();
        entity.add(new TreeStateComponent());
        entity.add(pc);
        entity.add(new SerializeEntityComponent(this));
        entity.add(new RenderComponent(RenderLayers.TILE_FRONT - 0.01f));
        ActivatorComponent ac = new ActivatorComponent();
        ac.layer = RenderLayers.ENTITY;
        //ac.activators.add(tt);
        entity.add(ac);
        BreakableComponent bc = new BreakableComponent();
        bc.setRequired(Components.TRANSFORM);
        bc.destructable.setMaterialLevel(1f).setRequiredTool(Tools.AXE);
        bc.breakable = new IBreakableEntity() {
            
            @Override
            public void collectDrops(World world, Random random, Entity entity, Array<ItemStack> drops) {
                drops.add(new ItemStack(Tiles.WOOD.getItemDropped(), 3 + random.nextInt(4)));
                drops.add(new ItemStack(Items.TWIG, random.nextInt(3)));
            }
            
            @Override
            public void onEntityBreak(World world, Entity entity, IBreaker breaker) {
            }
        };
        entity.add(bc);
        RandomTickComponent rtc = new RandomTickComponent();
        rtc.setRequired(Components.TRANSFORM, Components.TREESTATE);//Hmm
        rtc.chance = 0.00001;
        rtc.tickable = (world, ent) -> {
            if (Components.TREESTATE.get(ent).loose) {
                return;
            }
            TransformComponent tcc = Components.TRANSFORM.get(ent);
            //PhysicsComponent pcc = Components.PHYSICS.get(entity);
            float f0 = world.getWorldRandom().nextFloat();
            float f1 = world.getWorldRandom().nextFloat();
            ItemStack toDrop = new ItemStack(Items.TWIG, 1);
            toDrop.drop(world, tcc.position.x + f0 * 1.5f, tcc.position.y + 2 + f1 * 3);
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
                        if (tileSystem.getTile(Tile.toGlobalTile(tc.position.x + 1) + i, ngty, layer).isSolid()) {
                            return;
                        }
                    }
                    Components.TREESTATE.get(entity).loose = true;
                    Components.RENDER.get(entity).setLayer(RenderLayers.ENTITY);
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
