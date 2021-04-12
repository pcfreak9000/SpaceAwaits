package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

public class AnimatedTextureProvider implements ITextureProvider {
    
    private Animation<ITextureProvider> animation;
    private ITextureProvider currentKeyFrame;
    private float accum;
    
    public AnimatedTextureProvider(Animation<ITextureProvider> animation) {
        this.animation = animation;
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    private void ev(RendererEvents.UpdateAnimationEvent ev) {
        accum += ev.dt;
        float duration = this.animation.getAnimationDuration();
        if (accum > duration) {
            accum -= duration;
        }
        this.currentKeyFrame = this.animation.getKeyFrame(accum);
    }
    
    @Override
    public TextureRegion getRegion() {
        return this.currentKeyFrame.getRegion();
    }
    
}
