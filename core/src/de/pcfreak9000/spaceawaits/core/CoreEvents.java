package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.assets.AssetManager;

import de.omnikryptec.event.Event;

public class CoreEvents {
    
    public static class PlayEvent extends Event {
        
    }
    
    public static class InitEvent extends Event {
    }
    
    public static class PostInitEvent extends Event {
    }
    
    public static class ResourceEvent extends Event {
        public final AssetManager assetMgr;
        
        public ResourceEvent(AssetManager mgr) {
            this.assetMgr = mgr;
        }
    }
    
    public static class QueueResourcesEvent extends ResourceEvent {
        
        public QueueResourcesEvent(AssetManager mgr) {
            super(mgr);
        }
        
    }
    
    public static class UpdateResourcesEvent extends ResourceEvent {
        
        public UpdateResourcesEvent(AssetManager mgr) {
            super(mgr);
        }
        
    }
}
