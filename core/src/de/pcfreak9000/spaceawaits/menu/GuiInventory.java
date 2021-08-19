package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

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
                followmouse.setItemStack(clicked.getStack());
                InvUtil.extract(clicked.inventoryBacking, clicked.slotIndex);
            } else {
                ItemStack leftover = InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex, currentAttached);//This could be handled better...
                if (leftover != null && leftover.getCount() == currentAttached.getCount()) {
                    followmouse.setItemStack(clicked.getStack());
                    InvUtil.extract(clicked.inventoryBacking, clicked.slotIndex);
                    InvUtil.insert(clicked.inventoryBacking, clicked.slotIndex, leftover);
                } else {
                    followmouse.setItemStack(leftover);
                }
            }
        }
    };
    
    public GuiInventory(GameRenderer gameRenderer) {
        super(gameRenderer);
        this.slots = new Array<>();
        this.followmouse = new FollowMouseStack();
        this.followmouse.addToStage(stage);
    }
    
    protected Slot createSlot(IInventory backing, int slot) {
        Slot s = new Slot(backing, slot);//TODO dont create the slot here, this cant create subclasses of slot
        slots.add(s);
        s.addListener(slotClickListener);
        return s;
    }
    
    protected void removeSlot(Slot s) {
        slots.removeValue(s, true);
    }
    
}
