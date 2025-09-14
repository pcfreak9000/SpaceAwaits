package de.pcfreak9000.spaceawaits.content.entities;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.content.components.Components;
import de.pcfreak9000.spaceawaits.content.components.TreeStateComponent;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.core.assets.TextureProvider;
import de.pcfreak9000.spaceawaits.core.ecs.EntityFactory;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.ecs.content.ActivatorComponent;
import de.pcfreak9000.spaceawaits.core.ecs.content.RandomTickComponent;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.breaking.IBreakable;
import de.pcfreak9000.spaceawaits.world.breaking.IBreaker;
import de.pcfreak9000.spaceawaits.world.breaking.ecs.BreakableComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.OnNeighbourChangeComponent;
import de.pcfreak9000.spaceawaits.world.ecs.OnNeighbourChangeComponent.OnNeighbourTileChange;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TreeFactory implements EntityFactory {
    
    private static final TextureProvider tex = TextureProvider.get("sometree.png");
    
    @Override
    public Entity createEntity() {
        Entity entity = new EntityImproved();
        entity.add(new ChunkComponent());
        RenderRenderableComponent rec = new RenderRenderableComponent();
        rec.renderable = tex;
        rec.color = Color.WHITE;
        
        rec.width = 4;//50 / 16f;
        rec.height = 6;//222 / 16f;
        
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
        bc.breakable = new IBreakable() {
            
            @Override
            public void collectDrops(Engine world, Random random, Entity entity, Array<ItemStack> drops) {
                drops.add(new ItemStack(Tiles.WOOD.getItemDropped(), 3 + random.nextInt(4)));
                drops.add(new ItemStack(Items.TWIG, random.nextInt(3)));
            }
            
            @Override
            public void onEntityBreak(Engine world, Entity entity, IBreaker breaker) {
            }
        };
        entity.add(bc);
        RandomTickComponent rtc = new RandomTickComponent();
        rtc.setRequired(Components.TRANSFORM, Components.TREESTATE);//Hmm
        rtc.chance = 0.00001;
        rtc.tickable = (world, ent, random) -> {
            if (Components.TREESTATE.get(ent).loose) {
                return;
            }
            TransformComponent tcc = Components.TRANSFORM.get(ent);
            //PhysicsComponent pcc = Components.PHYSICS.get(entity);
            float f0 = random.nextFloat();
            float f1 = random.nextFloat();
            ItemStack toDrop = new ItemStack(Items.TWIG, 1);
            toDrop.drop(world, tcc.position.x + f0 * 1.5f, tcc.position.y + 2 + f1 * 3);
        };
        entity.add(rtc);
        OnNeighbourChangeComponent oncc = new OnNeighbourChangeComponent();
        oncc.setRequired(Components.PHYSICS, Components.TRANSFORM);
        oncc.onNeighbourTileChange = new OnNeighbourTileChange() {
            
            @Override
            public void onNeighbourTileChange(Engine world, TileSystem tileSystem, Entity entity, Tile newNeighbour,
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
                    b2BodyId b = Components.PHYSICS.get(entity).body.getBody();
                    Box2d.b2Body_SetType(b, b2BodyType.b2_dynamicBody);
                    //TODO change mask of body so it doesn't collide with player anymore or reincarnate it as sensor, idk
                }
            }
        };
        entity.add(oncc);
        return entity;
    }
    
}
