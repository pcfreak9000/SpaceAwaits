package de.pcfreak9000.spaceawaits.content.tiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.AnimatedTextureProvider;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileTorch extends Tile {
    
    private TextureProvider all = TextureProvider.get("torchAnimated.png");
    
    public TileTorch() {
        setDisplayName("Simple Torch");
        setSolid(false);
        setOpaque(false);
        setLightColor(Color.WHITE);
        setHardness(0);
        ITextureProvider[] ar = new ITextureProvider[7];
        for (int i = 0; i < ar.length; i++) {
            final int x = i;
            ar[i] = new ITextureProvider() {
                
                @Override
                public TextureRegion getRegion() {
                    TextureRegion tr = new TextureRegion(all.getRegion(), 0, x * 32, 32, 32);
                    return tr;
                }
            };
        }
        AnimatedTextureProvider antp = new AnimatedTextureProvider(
                new Animation<>(0.11f, new Array<>(ar), PlayMode.LOOP));
        setTextureProvider(antp);
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        Tile behind = layer == TileLayer.Front ? tileSystem.getTile(tx, ty, TileLayer.Back) : null;
        Tile below = tileSystem.getTile(tx, ty - 1, layer);
        return (behind != null && behind.isSolid()) || (below != null && below.isSolid());
    }
    
    @Override
    public void onNeighbourChange(World world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer) {
        if (!canPlace(gtx, gty, layer, world, tileSystem)) {
            tileSystem.removeTile(gtx, gty, layer);
            ItemStack toDrop = new ItemStack(getItemDropped(), getDroppedQuantity());//TODO maybe put this somewhere else? -> drops rework
            toDrop.drop(world, gtx, gty);
        }
    }
}
