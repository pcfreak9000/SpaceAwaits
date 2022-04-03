package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public class FollowMouseStack extends InputListener {
    
    private ActorItemStack actorItemStack;
    private Slot originSlot;
    
    public FollowMouseStack() {
        this.actorItemStack = new ActorItemStack();
        this.actorItemStack.setTouchable(Touchable.disabled);
    }
    
    //    public ActorItemStack getActorItemStack() {
    //        return actorItemStack;
    //    }
    
    public ItemStack getItemStack() {
        return actorItemStack.getItemStack();
    }
    
    public void setItemStack(ItemStack stack) {
        actorItemStack.setItemStack(stack);
        if (stack != null && !stack.isEmpty()) {
            this.actorItemStack.toFront();
        }
    }
    
    public void setSlotOrigin(Slot slot) {
        this.originSlot = slot;
    }
    
    public Slot getSlotOrigin() {
        return this.originSlot;
    }
    
    public boolean hasStack() {
        return actorItemStack.hasStack();
    }
    
    public void setBounds(float x, float y, float w, float h) {
        this.actorItemStack.setBounds(x - w / 2f, y - h / 2f, w, h);
    }
    
    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        if (actorItemStack.hasStack()) {
            actorItemStack.setPosition(event.getStageX() - actorItemStack.getWidth() / 2f,
                    event.getStageY() - actorItemStack.getHeight() / 2f);
        }
        return super.mouseMoved(event, x, y);
    }
    
    public void addToStage(Stage stage) {
        stage.addListener(this);
        stage.addActor(this.actorItemStack);
    }
}
