package de.pcfreak9000.spaceawaits.content.tiles;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.box2d.structs.b2Manifold;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileLadder extends Tile implements IContactListener {
    public TileLadder() {
        setDisplayName("Ladder");
        setTexture("ladder.png");
        setHardness(0.2f);
        setOpaque(false);
        setSolid(false);
    }
    
    @Override
    public IContactListener getContactListener() {
        return this;
    }
    
    @Override
    public void onNeighbourChange(Engine world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer, Random random) {
        if (!canPlace(gtx, gty, layer, world, tileSystem)) {
            tileSystem.removeTile(gtx, gty, layer);
            ItemStack toDrop = new ItemStack(getItemDropped(), getDroppedQuantity());//TODO maybe put this somewhere else? -> drops rework
            toDrop.drop(world, gtx, gty);
            //ItemStack.dropRandomInTile(getDropsBase(world, null, gtx, gty, layer), world, gtx, gty);
        }
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, Engine world, TileSystem tileSystem) {
        return layer == TileLayer.Front && tileSystem.getTile(tx, ty, TileLayer.Back).isSolid();
    }
    
    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, b2Manifold manifold, UnitConversion conv,
            Engine world) {
        if (other.isEntity() && Components.PLAYER_INPUT.has(other.getEntity())) {
            Components.ON_SOLID_GROUND.get(other.getEntity()).freemovementContacts++;
        }
        return false;
    }
    
    @Override
    public boolean endContact(UserDataHelper owner, UserDataHelper other, UnitConversion conv,
            Engine world) {
        if (other.isEntity() && Components.PLAYER_INPUT.has(other.getEntity())) {
            Components.ON_SOLID_GROUND.get(other.getEntity()).freemovementContacts--;
        }
        return false;
    }
    
}
