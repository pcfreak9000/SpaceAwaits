import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ItemRepairGun extends Item {
    
    public ItemRepairGun() {
        setMaxStackSize(1);
    }
    
    private final UserDataHelper udh = new UserDataHelper();
    
    private static final ComponentMapper<DamagedComponent> MAP = ComponentMapper.getFor(DamagedComponent.class);
    
    @Override
    public boolean onItemUse(Player player, ItemStack stackUsed, World world, int tilex, int tiley, float x, float y,
            TileLayer layer) {
        PhysicsSystem phys = world.getSystem(PhysicsSystem.class);
        Entity[] ent = new Entity[1];
        phys.queryAABB((fix, uc) -> {
            if (fix.testPoint(uc.in(x), uc.in(y))) {
                udh.set(fix.getUserData(), fix);
                if (udh.isEntity()) {
                    Entity e = udh.getEntity();
                    if (MAP.has(e)) {
                        ent[0] = e;
                        return true;
                    }
                }
            }
            return true;
        }, x - 0.01f, y - 0.01f, x + 0.01f, y + 0.01f);
        if (ent[0] != null) {
            DamagedComponent dc = MAP.get(ent[0]);
            dc.damage -= 0.00075f;
            NBTCompound nbt = stackUsed.getOrCreateNBT();
            nbt.putByte("bar", (byte) (nbt.getByteOrDefault("bar", (byte) 255) - 1));
            if (nbt.getByte("bar") == 0) {
                stackUsed.changeNumber(-1);
            }
            System.out.println(dc.damage);
            if (dc.damage <= 0) {
                ent[0].remove(DamagedComponent.class);
            }
            return true;
        }
        return false;
    }
}
