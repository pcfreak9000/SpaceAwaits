package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Action;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class UseAction implements Action {
    private static final ComponentMapper<PlayerInputComponent> mapper = ComponentMapper
            .getFor(PlayerInputComponent.class);
    
    @Override
    public Object getInputKey() {
        return EnumInputIds.Use;
    }
    
    @Override
    public boolean handle(float mousex, float mousey, World world, Entity source) {
        boolean backlayer = InptMgr.isPressed(EnumInputIds.BackLayerMod);
        TileLayer layer = backlayer ? TileLayer.Back : TileLayer.Front;
        Player player = mapper.get(source).player;
        //Current mouse stuff
        int tx = Tile.toGlobalTile(mousex);
        int ty = Tile.toGlobalTile(mousey);
        //get current item
        boolean used = false;
        ItemStack stack = player.getInventory().getSelectedStack();
        TileSystem tileSystem = world.getSystem(TileSystem.class);
        if (!used) {//TODO Move to activator in chunk entity?
            Tile clicked = tileSystem.getTile(tx, ty, TileLayer.Front);//Only allow using the front layer... (afaik backlayer doesnt support tile entities?)
            //onTileUse
            ItemStack cp = stack != null ? stack.cpy() : null;
            used = clicked.onTileUse(player, world, tileSystem, cp, tx, ty);
            player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
        }
        
        if (!used && player.getInventory().getSelectedStack() != null
                && !player.getInventory().getSelectedStack().isEmpty()) {
            //onItemUse
            if (stack != null && stack.getItem() != null) {
                ItemStack cp = stack.cpy();
                used = stack.getItem().onItemUse(player, cp, world, tx, ty, mousex, mousey, layer);//Hmmm... does the layer fit here?
                player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
            }
        }
        return used;
    }
    
}