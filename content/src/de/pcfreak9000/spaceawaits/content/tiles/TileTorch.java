package de.pcfreak9000.spaceawaits.content.tiles;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import de.pcfreak9000.spaceawaits.core.assets.AnimatedTextureProvider;
import de.pcfreak9000.spaceawaits.core.assets.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.assets.RegionedTextureProvider;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileLiquid;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileTorch extends Tile {
    
    public TileTorch() {
        setDisplayName("Simple Torch");
        setSolid(false);
        setOpaque(false);
        setFullTile(false);
        setLightColor(Color.WHITE);
        setHardness(0);
        //THIS IS SLIGHTLY LESS GARBAGE THAN BEFORE:
        ITextureProvider[] ar = new ITextureProvider[7];
        for (int i = 0; i < ar.length; i++) {
            ar[i] = new RegionedTextureProvider("torchAnimated.png", 0, i * 32, 32, 32);
        }
        AnimatedTextureProvider antp = new AnimatedTextureProvider(0.11f, ar, PlayMode.LOOP);
        setTextureProvider(antp);
    }
    
    @Override
    public boolean canBeReplacedBy(Tile t) {
        return t instanceof TileLiquid;
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, Engine world, TileSystem tileSystem) {
        Tile behind = layer == TileLayer.Front ? tileSystem.getTile(tx, ty, TileLayer.Back) : null;
        Tile below = tileSystem.getTile(tx, ty - 1, layer);
        return (behind != null && behind.isSolid() && behind.isFullTile())
                || (below != null && below.isSolid() && below.isFullTile());
    }
    
    @Override
    public void onNeighbourChange(Engine world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer, Random random) {
        if (!canPlace(gtx, gty, layer, world, tileSystem)) {
            tileSystem.removeTile(gtx, gty, layer);
            dropAsItemsInWorld(world, random, gtx, gty, layer);
        }
    }
}
