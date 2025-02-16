package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.EntitySystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

//Hmmmm... isJustPressed behaves awkward with fixed time step game loops. This is fixed now.
public class InventoryOpenerSystem extends EntitySystem implements RenderSystemMarker {
    
    private Player player;
    private boolean inGui;
    
    public InventoryOpenerSystem() {
    }
    
    @EventSubscription
    public void joined(WorldEvents.PlayerJoinedEvent ev) {
        this.player = ev.player;
    }
    
    @EventSubscription
    private void guioverlayev(RendererEvents.OpenGuiOverlay ev) {
        inGui = true;
    }
    
    @EventSubscription
    private void guioverlayev2(RendererEvents.CloseGuiOverlay ev) {
        inGui = false;
    }
    
    @Override
    public void update(float deltaTime) {
        if (InptMgr.isJustPressed(EnumInputIds.ToggleInventory)) {
            if (!inGui) {
                player.openInventory();
            }
        }
    }
}
