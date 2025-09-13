package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.WorldEvents;

//isJustPressed behaves awkward with fixed time step game loops. This is fixed now.
@RenderSystemMarker
public class InventoryHandlerSystem extends EntitySystem {
    
    private Player player;
    
    public InventoryHandlerSystem() {
    }
    
    @EventSubscription
    public void joined(WorldEvents.PlayerJoinedEvent ev) {
        this.player = ev.player;
    }
    
    @Override
    public void update(float deltaTime) {
        if (InptMgr.WORLD.isJustPressed(EnumInputIds.ToggleInventory)) {
            player.openInventory();
        }
        if (!InptMgr.WORLD.isPressed(EnumInputIds.CamZoom)) {
            int hotbarChecked = checkSelectHotbarSlot(player.getInventory().getSelectedSlot(), deltaTime);
            player.getInventory().setSelectedSlot(hotbarChecked);
        }
    }
    
    private int checkSelectHotbarSlot(int current, float dt) {
        if (InptMgr.WORLD.isLocked()) {
            return current;
        }
        for (int i = Keys.NUM_1; i <= Keys.NUM_9; i++) {
            if (Gdx.input.isKeyPressed(i)) {
                return i - Keys.NUM_1;
            }
        }
        
        float scroll = InptMgr.getScrollY();
        int v = (int) Math.signum(scroll);
        int select = current + v;
        if (select < 0) {
            return select + 9;
        }
        return select % 9;
        
    }
}
