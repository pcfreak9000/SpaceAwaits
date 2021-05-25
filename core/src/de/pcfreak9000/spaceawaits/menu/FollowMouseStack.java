package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class FollowMouseStack extends InputListener {
    
    private ActorItemStack actorItemStack;
    
    public FollowMouseStack() {
        this.actorItemStack = new ActorItemStack();
    }
    
    public ActorItemStack getActorItemStack() {
        return actorItemStack;
    }
    
    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        if (actorItemStack.hasStack()) {
            actorItemStack.setPosition(event.getStageX(), event.getStageY());
        }
        return super.mouseMoved(event, x, y);
    }
}
