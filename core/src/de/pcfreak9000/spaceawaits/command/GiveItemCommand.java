package de.pcfreak9000.spaceawaits.command;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.registry.Registry;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "give")
public class GiveItemCommand implements Runnable {
    @Parameters(index = "0")
    private String id;
    @Parameters(index = "1", defaultValue = "1")
    private int amount;
    
    @Override
    public void run() {
        Item i = Registry.ITEM_REGISTRY.get(id);
        if (i != null) {
            IInventory inv = SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer().getInventory();
            InvUtil.insert(inv, new ItemStack(i, amount));
        }
    }
    
}
