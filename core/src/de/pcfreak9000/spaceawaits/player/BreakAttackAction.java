package de.pcfreak9000.spaceawaits.player;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Action;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class BreakAttackAction implements Action {

    private final ITileBreaker br = new ITileBreaker() {
        
        @Override
        public float getSpeed() {
            return 1;
        }
        
        @Override
        public float getMaterialLevel() {
            return 0;
        }
        
        @Override
        public void onTileBreak(int tx, int ty, TileLayer layer, Tile tile, World world, TileSystem tileSystem,
                Array<ItemStack> drops, Random random) {
        }
        
        @Override
        public boolean canBreak(int tx, int ty, TileLayer layer, Tile tile, World world, TileSystem tilesystem) {
            return true;
        }
    };
    
    @Override
    public Object getInputKey() {
        return EnumInputIds.BreakAttack;
    }
    
    @Override
    public boolean handle(float mousex, float mousey, World world, Entity source) {
        Player player = Components.PLAYER_INPUT.get(source).player;
        boolean backlayer = InptMgr.isPressed(EnumInputIds.BackLayerMod);
        TileLayer layer = backlayer ? TileLayer.Back : TileLayer.Front;
        int tx = Tile.toGlobalTile(mousex);
        int ty = Tile.toGlobalTile(mousey);
        ItemStack stack = player.getInventory().getSelectedStack();
        boolean used = false;
        if (stack != null && stack.getItem() != null) {
            ItemStack cp = stack.cpy();
            used = stack.getItem().onItemAttack(player, cp, world, tx, ty, mousex, mousey);
            player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
        }
        if (!used) {
            world.getSystem(TileSystem.class).breakTile(tx, ty, layer, br);
        }
        return used;
    }
    
}
