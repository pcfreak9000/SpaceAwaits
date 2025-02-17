package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.InventoryPlayer;

public class GuiInventory extends GuiOverlay {
    
    private Array<Slot> slots;
    //FollowMouseStack+ClickListener...
    private FollowMouseStack followmouse;
    
    protected IInventory inventoryBackingMain;
    
    public GuiInventory() {
        this(null);
    }
    
    public GuiInventory(IInventory invBackingMain) {
        this.inventoryBackingMain = invBackingMain;
        this.slotClickListener.setButton(-1);
    }
    
    private ClickListener slotClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Slot clicked = (Slot) event.getListenerActor();
            followmouse.setBounds(event.getStageX(), event.getStageY(), Slot.SIZE * 0.9f, Slot.SIZE * 0.9f);
            ItemStack currentAttached = followmouse.getItemStack();
            if (currentAttached == null || currentAttached.isEmpty()) {
                if (clicked.canTake()) {
                    if (inventoryBackingMain != null && InptMgr.isPressed(EnumInputIds.INV_MOD)) {
                        ItemStack stack = InvUtil.extract(clicked.inventoryBacking, clicked.slotIndex);
                        IInventory target = clicked.inventoryBacking == player.getInventory() ? inventoryBackingMain
                                : player.getInventory();
                        ItemStack leftover = InvUtil.insert(target, stack);
                        if (!ItemStack.isEmptyOrNull(leftover)) {
                            InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex, leftover);
                        }
                    } else {
                        followmouse.setItemStack(clicked.getStack());
                        followmouse.setSlotOrigin(clicked);
                        InvUtil.extract(clicked.inventoryBacking, clicked.slotIndex);
                    }
                }
            } else {
                //Maybe handle individual stuff in slots themselfes?
                if (clicked.canPut()) {
                    if (event.getButton() == Buttons.LEFT) {
                        ItemStack leftover = InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex,
                                currentAttached);//This could be handled better...
                        if (leftover != null && leftover.getCount() == currentAttached.getCount()
                                && clicked.inventoryBacking.isItemValidForSlot(clicked.slotIndex, leftover)) {
                            followmouse.setItemStack(clicked.getStack());
                            InvUtil.extract(clicked.inventoryBacking, clicked.slotIndex);
                            InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex, leftover);
                        } else {
                            followmouse.setItemStack(leftover);
                        }
                    } else if (event.getButton() == Buttons.RIGHT) {
                        ItemStack in = currentAttached.sub(1);
                        ItemStack leftover = InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex, in);//This could be handled better...
                        if (!ItemStack.isEmptyOrNull(leftover)) {
                            currentAttached.changeNumber(leftover.getCount());
                        }
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
                player.dropWhenPossible(stuff);
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
