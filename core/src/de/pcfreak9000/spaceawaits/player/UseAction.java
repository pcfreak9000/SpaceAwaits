package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.content.Action;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class UseAction implements Action {
    
    @Override
    public boolean isContinuous() {
        return true;
    }
    
    @Override
    public Object getInputKey() {
        return EnumInputIds.Use;
    }
    
    @Override
    public boolean handle(float mousex, float mousey, Engine world, Entity source) {
        boolean backlayer = InptMgr.WORLD.isPressed(EnumInputIds.BackLayerMod);
        TileLayer layer = backlayer ? TileLayer.Back : TileLayer.Front;
        Player player = Components.PLAYER_INPUT.get(source).player;
        //Current mouse stuff
        int tx = Tile.toGlobalTile(mousex);
        int ty = Tile.toGlobalTile(mousey);
        //get current item
        boolean used = false;
        ItemStack stack = player.getInventory().getSelectedStack();
        TileSystem tileSystem = world.getSystem(TileSystem.class);
        if (!used && player.isInReachFromHand(mousex, mousey,
                (ItemStack.isEmptyOrNull(stack) ? player.getReach() : stack.getItem().getReach(player, stack)))) {//Move to activator in chunk entity? -> chunk isnt filled with fixtures but they are used for detection, so no (for now)
            Tile clicked = tileSystem.getTile(tx, ty, layer);//FIXME can't use backlayer tiles if front layer is blocking!! (reuse from building?)
            //onTileUse
            ItemStack cp = stack != null ? stack.cpy() : null;
            if (InptMgr.WORLD.isJustPressed(getInputKey())) {
                used |= clicked.onTileJustUse(player, world, tileSystem, stack, tx, ty, layer);
            }
            used |= clicked.onTileUse(player, world, tileSystem, cp, tx, ty, layer);
            player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
        }
        if (!used && !ItemStack.isEmptyOrNull(stack)
                && player.isInReachFromHand(mousex, mousey, stack.getItem().getMaxRangeUse(player, stack))) {
            //onItemUse
            if (stack != null && stack.getItem() != null) {
                ItemStack cp = stack.cpy();
                if (InptMgr.WORLD.isJustPressed(getInputKey())) {
                    used |= stack.getItem().onItemJustUse(player, cp, world, mousex, mousey, ty, ty, layer);
                }
                used |= stack.getItem().onItemUse(player, cp, world, mousex, mousey, tx, ty, layer);//Hmmm... does the layer fit here?
                player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
            }
        }
        return used;
    }
    
}
