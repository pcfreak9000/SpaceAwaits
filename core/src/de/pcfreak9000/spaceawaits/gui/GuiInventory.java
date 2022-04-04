package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.InventoryPlayer;

public class GuiInventory extends GuiOverlay {
    
    private Array<Slot> slots;
    //FollowMouseStack+ClickListener...
    private FollowMouseStack followmouse;
    private ClickListener slotClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Slot clicked = (Slot) event.getListenerActor();
            followmouse.setBounds(event.getStageX(), event.getStageY(), Slot.SIZE, Slot.SIZE);
            ItemStack currentAttached = followmouse.getItemStack();
            if (currentAttached == null || currentAttached.isEmpty()) {
                if (clicked.canTake()) {
                    followmouse.setItemStack(clicked.getStack());
                    followmouse.setSlotOrigin(clicked);
                    InvUtil.extract(clicked.inventoryBacking, clicked.slotIndex);
                }
            } else {
                if (clicked.canPut()) {
                    ItemStack leftover = InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex, currentAttached);//This could be handled better...
                    if (leftover != null && leftover.getCount() == currentAttached.getCount()
                            && clicked.inventoryBacking.isItemValidForSlot(clicked.slotIndex, leftover)) {
                        followmouse.setItemStack(clicked.getStack());
                        InvUtil.extract(clicked.inventoryBacking, clicked.slotIndex);
                        InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex, leftover);
                    } else {
                        followmouse.setItemStack(leftover);
                    }
                }
            }
        }
    };
    
    protected Table createPlayerInventoryTable() {
        InventoryPlayer inv = player.getInventory();
        Table table = new Table();
        table.align(Align.center);
        for (int i = 9; i < inv.slots(); i++) {
            if (i % 9 == 0) {
                table.row();
            }
            table.add(registerSlot(new Slot(inv, i))).pad(0.5f);
        }
        table.row();
        for (int i = 0; i < 9; i++) {
            table.add(registerSlot(new Slot(inv, i))).pad(0.5f).padTop(10f);
        }
        return table;
    }
    
    @Override
    protected void create() {
        this.slots = new Array<>();
        this.followmouse = new FollowMouseStack();
        this.followmouse.addToStage(stage);
    }
    
    protected Slot registerSlot(Slot slot) {
        slot.addListener(slotClickListener);
        slots.add(slot);
        return slot;
    }
    
    @Override
    public void onClosed() {
        super.onClosed();
        if (followmouse.hasStack()) {
            Slot origin = followmouse.getSlotOrigin();
            ItemStack stuff = followmouse.getItemStack();
            if (origin != null) {
                stuff = InvUtil.insert(origin.inventoryBacking, origin.slotIndex, followmouse.getItemStack());
                if (!ItemStack.isEmptyOrNull(stuff)) {
                    stuff = InvUtil.insert(origin.inventoryBacking, stuff);
                }
            }
            if (!ItemStack.isEmptyOrNull(stuff)) {
                stuff = InvUtil.insert(player.getInventory(), stuff);
            }
            if (!ItemStack.isEmptyOrNull(stuff)) {
                //TODO: drop itemstack?
            }
            followmouse.setItemStack(null);
        }
    }
    
    @Deprecated
    protected Slot createSlot(IInventory backing, int slot) {
        Slot s = new Slot(backing, slot);
        registerSlot(s);
        return s;
    }
    
    protected void removeSlot(Slot s) {
        slots.removeValue(s, true);
    }
    
}
