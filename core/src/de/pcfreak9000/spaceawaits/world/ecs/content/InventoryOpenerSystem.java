package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.EntitySystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
//Hmmmm... isJustPressed behaves awkward with fixed time step game loops
public class InventoryOpenerSystem extends EntitySystem implements RenderSystemMarker {
    
    private final GameRenderer worldRend;
    private Player player;
    
    public InventoryOpenerSystem(GameRenderer worldRend, World world) {
        this.worldRend = worldRend;
        world.getWorldBus().register(this);
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
