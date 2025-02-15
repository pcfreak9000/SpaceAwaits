package de.pcfreak9000.spaceawaits.crafting;

import de.pcfreak9000.spaceawaits.core.ecs.content.Tickable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

public abstract class MachineBase implements Tickable {
    
    @NBTSerialize(key = "workticksleft")
    protected int workTicksLeft;
    
    @NBTSerialize(key = "prog")
    protected int progress;
    
    protected abstract int getProcessingTicksRequired();
    
    protected abstract void refuel();
    
    protected abstract void finishProcess();
    
    protected abstract boolean canProcess();
    
    public float getRelativeProgress() {
        int t = getProcessingTicksRequired();
        if (t == -1) {
            return 0;
        } else if (t == 0) {
            return 1;
        }
        return progress / (float) t;
    }
    
    @Override
    public void tick(float dtime, long tickIndex) {
        if (canProcess()) {
            if (requireWorkTicks()) {
                //increase progress
                this.progress += 1;
                //check for interuptions or put the result
                if (this.progress >= this.getProcessingTicksRequired()) {
                    finishProcess();
                }
            }
        } else {
            this.progress = 0;
        }
        useUpWorkTicks();
    }
    
    protected boolean requireWorkTicks() {
        if (workTicksLeft <= 0) {
            //refuel but only if there is an active recipe
            refuel();
        }
        if (workTicksLeft > 0) {
            return true;
        } else {
            this.progress = Math.max(0, this.progress - 1);//Hmm
            return false;
        }
    }
    
    protected void useUpWorkTicks() {
        //remove burntime if there is any left even if there isn't an active recipe
        if (workTicksLeft > 0) {
            workTicksLeft = Math.max(0, workTicksLeft - 1);
        }
    }
}
