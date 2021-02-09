package de.pcfreak9000.spaceawaits.world.light;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.async.AsyncTask;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class PixelPointLightTask2 implements AsyncTask<Void> {
    private static class LightState {
        private int i, j;
        private int oi, oj;
    }
    
    private float threshold = 0.02f;
    private WorldAccessor world;
    private Consumer<Pixmap> consumer;
    private int atx;
    private int aty;
    private int areawidth;
    private int areaheight;
    
    public PixelPointLightTask2(WorldAccessor world, Consumer<Pixmap> consumer, int tx, int ty, int areawidth, int areaheight) {
        this.world = world;
        this.consumer = consumer;
        this.atx = tx;
        this.aty = ty;
        this.areawidth = areawidth;
        this.areaheight = areaheight;
    }
    
    private boolean checkBounds(int i, int j) {
        return i >= 0 && i < areawidth && j >= 0 && j < areaheight;
    }
    
    @Override
    public Void call() throws Exception {
        Pixmap pix = new Pixmap(areawidth, areaheight, Format.RGB888);
        Color[][] lightvalues = new Color[pix.getWidth()][pix.getHeight()];
        Queue<LightState> queue = new ArrayDeque<>();
        for (int i = 0; i < areawidth; i++) {
            for (int j = 0; j < areaheight; j++) {
                int gtx = i + atx;
                int gty = j + aty;
                if (world.getMeta().inBounds(gtx, gty)) {
                    Tile tile = world.getTile(gtx, gty);
                    Tile backTile = world.getTileBackground(gtx, gty);
                    Color light = null;
                    if (tile == null || !tile.isOpaque()) {
                        if (backTile == null || !backTile.isOpaque()) {
                            light = this.world.getAmbientLight().getAmbientLightNew(gtx, gty);
                        }
                        if (backTile != null && backTile.hasLight()) {
                            Color backTileLight = backTile.getLightColor();
                            if (light == null) {
                                light = backTileLight.cpy();
                            } else {
                                light.mul(backTile.getLightTransmission());
                                light.add(backTileLight);//Max instead as the propagation also uses max?
                            }
                        }
                    }
                    if (tile != null && tile.hasLight()) {
                        Color tileLight = tile.getLightColor();
                        if (light == null) {
                            light = tileLight.cpy();
                        } else {
                            light.mul(tile.getLightTransmission());
                            light.add(tileLight);//Max instead as the propagation also uses max?
                        }
                    }
                    if (light != null) {
                        LightState state = new LightState();
                        state.i = i;
                        state.j = j;
                        state.oi = i;
                        state.oj = j;
                        lightvalues[state.i][state.j] = light;
                        queue.add(state);
                    }
                }
            }
        }
        while (!queue.isEmpty()) {
            LightState state = queue.poll();
            help(queue, state, state.i + 1, state.j, lightvalues);
            help(queue, state, state.i - 1, state.j, lightvalues);
            help(queue, state, state.i, state.j + 1, lightvalues);
            help(queue, state, state.i, state.j - 1, lightvalues);
            pix.setColor(lightvalues[state.i][state.j]);//Test if modified lightstates should be drawn in a different for-loop or here
            pix.drawPixel(state.i, pix.getHeight() - 1 - state.j);
        }
        //Gdx.app.postRunnable(() -> {
        consumer.accept(pix);
        //});
        return null;
    }
    
    private void help(Queue<LightState> queue, LightState front, int i, int j, Color[][] intens) {
        if (!checkBounds(i, j) || (front.oi == i && front.oj == j)) {
            return;
        }
        int tx = atx + i;
        int ty = aty + j;
        Tile tile = Tile.EMPTY;//Hmmm...
        if (world.getMeta().inBounds(tx, ty)) {
            tile = world.getTile(tx, ty);
        }
        float loss = tile.getLightTransmission();
        float nr = loss * intens[front.i][front.j].r;
        float ng = loss * intens[front.i][front.j].g;
        float nb = loss * intens[front.i][front.j].b;
        Color old = intens[i][j];
        if ((old == null || nr > old.r || ng > old.g || nb > old.b)
                && (nr > threshold || ng > threshold || nb > threshold)) {
            if (old == null) {
                intens[i][j] = new Color(nr, ng, nb, 1);
            } else {
                old.r = Mathf.max(nr, old.r);
                old.g = Mathf.max(ng, old.g);
                old.b = Mathf.max(nb, old.b);
            }
            LightState newState = new LightState();
            newState.i = i;
            newState.j = j;
            newState.oi = front.i;
            newState.oj = front.j;
            queue.add(newState);
        }
        
    }
    
}
