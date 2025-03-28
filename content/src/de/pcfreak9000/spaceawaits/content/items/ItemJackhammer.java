package de.pcfreak9000.spaceawaits.content.items;

import com.badlogic.ashley.core.Engine;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.content.modules.IModuleEnergy;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemHelper;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.breaking.IBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemJackhammer extends Item {
    
    private static final float RANGE = 5;
    private final BreakerTools breaker = new BreakerTools(Tools.PICKAXE, 6f, 3f);
    
    public ItemJackhammer() {
        this.setMaxStackSize(1);
        this.setDisplayName("Jackhammer");
        this.setTexture("jackhammer.png");
    }
    
    @Override
    public float getMaxRangeBreakAttack(Player player, ItemStack stack) {
        return RANGE;
    }
    
    @Override
    public float getMaxRangeUse(Player player, ItemStack stackUsed) {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean onItemJustUse(Player player, ItemStack stackUsed, Engine world, float x, float y, int tilex,
            int tiley, TileLayer layer) {
        player.openContainer(new ContainerJackhammer(stackUsed));
        return true;
    }
    
    @Override
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, Engine world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        ItemStack bat = ItemHelper.getNBTStoredItemStack(stackUsed, ContainerJackhammer.COMPOUNDID);
        if (ItemStack.isEmptyOrNull(bat)) {
            return false;
        }
        IModuleEnergy enmod = bat.getItem().getModule(IModuleEnergy.ID);
        float chargeNeeded = 50f * GameScreen.STEPLENGTH_SECONDS;
        if (enmod.getCurrentCharge(bat) <= 0f) {
            return false;
        }
        float f = tiles.breakTile(tx, ty, layer, breaker);
        if (f != IBreaker.ABORTED_BREAKING) {
            enmod.changeCharge(bat, -chargeNeeded);
            ItemHelper.setNBTStoredItemStack(stackUsed, ContainerJackhammer.COMPOUNDID, bat);
        }
        return f != IBreaker.ABORTED_BREAKING;
    }
    
}
