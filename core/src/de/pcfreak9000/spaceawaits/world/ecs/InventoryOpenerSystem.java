package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.EntitySystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;

//Hmmmm... isJustPressed behaves awkward with fixed time step game loops. This is fixed now.
public class InventoryOpenerSystem extends EntitySystem implements RenderSystemMarker {
    
    private final GameScreen worldRend;
    private Player player;
    
    public InventoryOpenerSystem(GameScreen worldRend, World world) {
        this.worldRend = worldRend;
    }
    
    @EventSubscription
    public void joined(WorldEvents.PlayerJoinedEvent ev) {
        this.player = ev.player;
    }
    
    @Override
    public void update(float deltaTime) {
        if (InptMgr.isJustPressed(EnumInputIds.ToggleInventory)) {
            if (!worldRend.isGuiContainerOpen()) {
                player.openInventory();
            }
        }
    }
}
