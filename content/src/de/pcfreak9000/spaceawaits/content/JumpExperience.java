package de.pcfreak9000.spaceawaits.content;

import com.badlogic.gdx.math.Interpolation;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.content.JumpExperience.Data;
import de.pcfreak9000.spaceawaits.knowledge.Experience;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;
import de.pcfreak9000.spaceawaits.world.WorldEvents.PlayerJumpEvent;

public class JumpExperience extends Experience {
    
    @NBTSerialize(key = "jedata")
    public static class Data {
        @NBTSerialize(key = "jcount")
        public long jumps;
        
        @EventSubscription
        private void evv(PlayerJumpEvent ev) {
            Data d = ev.player.getExperience().getDataHolder(GameMod.JUMP_EXPERIENCE);
            float strength = GameMod.JUMP_EXPERIENCE.getStrength(d.jumps);
            ev.strength = strength;
            d.jumps++;
        }
    }
    
    public JumpExperience() {
        setDisplayName("Jump training");
    }
    
    @Override
    public Object createDataHolder() {
        return new Data();
    }
    
    public float getStrength(long jumps) {
        return Interpolation.exp5.apply(1f, 2f, Mathf.clamp01(jumps / 500f));
    }
}
