package de.pcfreak9000.spaceawaits.content.tiles;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
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
    public void onNeighbourChange(World world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer) {
        if (!canPlace(gtx, gty, layer, world, tileSystem)) {
            tileSystem.removeTile(gtx, gty, layer);
            ItemStack toDrop = new ItemStack(getItemDropped(), getDroppedQuantity());//TODO maybe put this somewhere else? -> drops rework
            toDrop.drop(world, gtx, gty);
            //ItemStack.dropRandomInTile(getDropsBase(world, null, gtx, gty, layer), world, gtx, gty);
        }
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        return layer == TileLayer.Front && tileSystem.getTile(tx, ty, TileLayer.Back).isSolid();
    }
    
    @Override
    public boolean beginContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            World world) {
        if (other.isEntity() && Components.PLAYER_INPUT.has(other.getEntity())) {
            Components.ON_SOLID_GROUND.get(other.getEntity()).solidGroundContacts++;
        }
        return false;
    }
    
    @Override
    public boolean endContact(UserDataHelper owner, UserDataHelper other, Contact contact, UnitConversion conv,
            World world) {
        if (other.isEntity() && Components.PLAYER_INPUT.has(other.getEntity())) {
            Components.ON_SOLID_GROUND.get(other.getEntity()).solidGroundContacts--;
        }
        return false;
    }
    
    @Override
    public boolean preSolve(UserDataHelper owner, UserDataHelper other, Contact contact, Manifold oldManifold,
            UnitConversion conv, World world) {
        return false;
    }
    
    @Override
    public boolean postSolve(UserDataHelper owner, UserDataHelper other, Contact contact, ContactImpulse impulse,
            UnitConversion conv, World world) {
        return false;
    }
}
