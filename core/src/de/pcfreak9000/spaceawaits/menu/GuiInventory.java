package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class GuiInventory extends GuiOverlay {
    
    private Array<Slot> slots;
    private FollowMouseStack followmouse;
    
    public GuiInventory(GameRenderer gameRenderer) {
        super(gameRenderer);
        slots = new Array<>();
        this.followmouse = new FollowMouseStack();
        stage.addListener(followmouse);
        stage.addActor(this.followmouse.getActorItemStack());
        this.followmouse.getActorItemStack().setTouchable(Touchable.disabled);
    }
    
    protected Slot createSlot(IInventory backing, int slot) {
        Slot s = new Slot(backing, slot);//TODO dont create the slot here, this cant create subclasses of slot
        slots.add(s);
        s.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                followmouse.getActorItemStack().setBounds(event.getStageX(), event.getStageY(), Slot.SIZE, Slot.SIZE);
                
                followmouse.getActorItemStack().setItemStack(s.getStack());
                followmouse.getActorItemStack().toFront();
            }
        });
        return s;
    }
    
    protected void removeSlot(Slot s) {
        slots.removeValue(s, true);
    }
    
}
