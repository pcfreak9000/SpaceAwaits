package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.utils.Disposable;

public abstract class DynamicAsset implements Disposable {
    
    //save scale: TBD
    //world/unchunk scale: create when world is created (and set as rendering, for the future when there might be multiple worlds)
    //                     dispose when world is unloaded (or when set as not rendering?)
    //entity/chunk scale: dynamicassetcomponent?
    
    private int depth = 0;
    
    public final void create() {
        if (depth == 0) {
            createInternal();
        }
        depth++;
    }
    
    @Override
    public final void dispose() {
        depth--;
        if (depth == 0) {
            disposeInternal();
        }
    }
    
    protected abstract void createInternal();
    
    protected abstract void disposeInternal();
}
