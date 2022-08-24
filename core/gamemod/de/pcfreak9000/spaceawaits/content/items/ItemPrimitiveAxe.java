package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.BreakingComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemPrimitiveAxe extends Item {
    
    public ItemPrimitiveAxe() {
        this.setMaxStackSize(1);
        this.setDisplayName("Primitive Axe");
        this.setTexture("shitty_axe.png");
    }
    
    @Override
    public boolean onItemAttack(Player player, ItemStack stackUsed, World world, int tx, int ty, float x, float y) {
        PhysicsSystem phys = world.getSystem(PhysicsSystem.class);
        Array<Object> ent = phys.queryXY(x, y,
                (udh, uc) -> udh.isEntity() && Components.BREAKABLE.has(udh.getEntity()));
        if (ent.size > 0) {
            Entity entity = (Entity) ent.get(0);
            BreakingComponent bc = Components.BREAKING.get(entity);
            if (bc == null) {
                bc = new BreakingComponent();
                entity.add(bc);
            }
            bc.addProgress += 1f;
            return true;
        }
        return false;
    }
    
    private final ITileBreaker tilebreaker = new ITileBreaker() {
        
        @Override
        public float getSpeed() {
            return 15;
        }
        
        @Override
        public float getMaterialLevel() {
            return Float.POSITIVE_INFINITY;
        }
        
        @Override
        public void onTileBreak(int tx, int ty, TileLayer layer, Tile tile, World world, TileSystem tileSystem,
                Array<ItemStack> drops, Random random) {
        }
        
        @Override
        public boolean canBreak(int tx, int ty, TileLayer layer, Tile tile, World world, TileSystem tileSystem) {
            return true;
        }
    };
}
