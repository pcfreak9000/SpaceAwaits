package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.content.Action;
import de.pcfreak9000.spaceawaits.world.breaking.InstantBreaker;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TestExplodeTilesAction implements Action {
    
    @Override
    public boolean isContinuous() {
        return true;
    }
    
    @Override
    public Object getInputKey() {
        return EnumInputIds.TestExplodeTiles;
    }
    
    @Override
    public boolean handle(float mousex, float mousey, Engine world, Entity source) {
        if (!Components.PLAYER_INPUT.get(source).player.getGameMode().isTesting) {
            return false;
        }
        boolean backlayer = InptMgr.WORLD.isPressed(EnumInputIds.BackLayerMod);
        TileLayer layer = backlayer ? TileLayer.Back : TileLayer.Front;
        int txm = Tile.toGlobalTile(mousex);
        int tym = Tile.toGlobalTile(mousey);
        TileSystem tileSystem = world.getSystem(TileSystem.class);
        final int rad = 3;
        for (int i = -rad; i <= rad; i++) {
            for (int j = -rad; j <= rad; j++) {
                if (Mathf.square(i) + Mathf.square(j) <= Mathf.square(rad)) {
                    int tx = txm + i;
                    int ty = tym + j;
                    tileSystem.breakTile(tx, ty, layer, InstantBreaker.INSTANCE);
                }
            }
        }
        return true;
    }
    
}
