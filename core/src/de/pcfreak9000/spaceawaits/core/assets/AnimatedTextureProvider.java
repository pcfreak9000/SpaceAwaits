package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
//TODO? maybe define animations in files instead of this, so a resourcepack could animate anything in theory...
public class AnimatedTextureProvider implements ITextureProvider {
    
    private Animation<ITextureProvider> animation;
    private ITextureProvider currentKeyFrame;
    private float accum;
    
    public AnimatedTextureProvider(float frameduration, ITextureProvider[] ar, PlayMode playmode) {
        this(frameduration, new Array<>(ar), playmode);
    }
    
    public AnimatedTextureProvider(float frameduration, Array<? extends ITextureProvider> tex, PlayMode playmode) {
        this(new Animation<>(frameduration, tex, playmode));
    }
    
    public AnimatedTextureProvider(Animation<ITextureProvider> animation) {
        this.animation = animation;
        SpaceAwaits.BUS.register(this);
    }
    
    private void updateAnimation(float dt) {
        accum += dt;
        float duration = this.animation.getAnimationDuration();
        if (accum > duration) {
            accum -= duration;
        }
        this.currentKeyFrame = this.animation.getKeyFrame(accum);
    }
    
    @EventSubscription
    private void ev(RendererEvents.UpdateAnimationEvent ev) {
        updateAnimation(ev.dt);
    }
    
    @Override
    public TextureRegion getRegion() {
        return this.currentKeyFrame.getRegion();
    }
    
}
