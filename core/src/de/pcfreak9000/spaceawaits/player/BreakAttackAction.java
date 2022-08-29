package de.pcfreak9000.spaceawaits.player;

import java.util.Comparator;
import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Action;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class BreakAttackAction implements Action {
    
    private static final Comparator<Object> ENTITY_COMPARATOR = (o0, o1) -> {
        Entity e0 = (Entity) o0;
        Entity e1 = (Entity) o1;
        if (Components.RENDER.has(e0) && Components.RENDER.has(e1)) {
            float dif = Components.RENDER.get(e1).layer - Components.RENDER.get(e0).layer;
            return (int) Math.signum(dif);
        }
        return 0;
    };
    
    private final IBreaker br = new IBreaker() {
        
        @Override
        public float breakIt(World world, Destructible breakable, float f) {
            return 1f / breakable.getHardness();
        }
        
        @Override
        public boolean canBreak(World world, Destructible breakable) {
            return breakable.getMaterialLevel() == 0.0f;
        }
        
        @Override
        public void onBreak(World world, Destructible breakable, Array<ItemStack> drops, Random random) {
        }
        
    };
    
    @Override
    public boolean isContinuous() {
        return true;
    }
    
    @Override
    public Object getInputKey() {
        return EnumInputIds.BreakAttack;
    }
    
    @Override
    public boolean handle(float mousex, float mousey, World world, Entity source) {
        TileSystem tiles = world.getSystem(TileSystem.class);
        Player player = Components.PLAYER_INPUT.get(source).player;
        boolean backlayer = InptMgr.isPressed(EnumInputIds.BackLayerMod);
        TileLayer layer = backlayer ? TileLayer.Back : TileLayer.Front;
        int tx = Tile.toGlobalTile(mousex);
        int ty = Tile.toGlobalTile(mousey);
        ItemStack stack = player.getInventory().getSelectedStack();
        boolean used = false;
        //find the entity that got hit
        Array<Object> ent = world.getSystem(PhysicsSystem.class).queryXY(mousex, mousey, (udh, uc) -> udh.isEntity()
                && udh.getEntity() != source && !Components.ITEM_STACK.has(udh.getEntity()));//Can't hit yourself or items...
        //What if there is an entity but without a body?? the tile behind it would be broken, might not be nice
        ent.sort(ENTITY_COMPARATOR);
        if (!ItemStack.isEmptyOrNull(stack)
                && player.isInReachFromHand(mousex, mousey, stack.getItem().getMaxRangeBreakAttack(player, stack))) {
            ItemStack cp = stack.cpy();
            if (stack.getItem().isSpecialBreakAttack()) {
                used = stack.getItem().onItemSpecialBreakAttack(player, cp, world, mousex, mousey, tx, ty, layer);
            } else {
                if (!ent.isEmpty()) {
                    //find out if the entity if there is one can be broken or attacked
                    Entity first = (Entity) ent.get(0);
                    used = stack.getItem().onItemBreakAttackEntity(player, cp, world, mousex, mousey, first);
                } else {
                    if (layer == TileLayer.Front || !tiles.getTile(tx, ty, TileLayer.Front).isSolid()) {
                        //try do tilestuff, first check frontlayer and then backlayer, also see "layer" 
                        used = stack.getItem().onItemBreakTile(player, cp, world, mousex, mousey, tiles, tx, ty, layer);
                    }
                }
            }
            player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
        }
        if (!player.isInReachFromHand(mousex, mousey,
                (ItemStack.isEmptyOrNull(stack) ? player.getReach() : stack.getItem().getReach(player, stack)))) {
            return false;
        }
        if (!used) {
            if (!ent.isEmpty()) {
                Entity first = (Entity) ent.get(0);
                //checks are done by breakEntity, thats sufficient in this case
                float prog = world.breakEntity(br, first);
                if (prog == IBreaker.ABORTED_BREAKING) {
                    //default attack here?
                }
            } else {
                //checks are done by breakTile, thats sufficient in this case
                tiles.breakTile(tx, ty, layer, br);
            }
            //also technically this uses stuff???? but this is default behaviour, and maybe some none-default behaviour awaits
        }
        return used;
    }
    
}
