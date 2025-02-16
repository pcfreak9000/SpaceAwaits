package de.pcfreak9000.spaceawaits.world.light;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncTask;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

@Deprecated
public class PixelPointLightTask implements AsyncTask<Void> {
    private static class LightState {
        private float value;
        private Color light;
        private int i, j;
        private int index;
    }
    
    private float threshold = 0.1f;
    private int lightConstant = 50;
    private World world;
    private TileSystem tiles;
    private Consumer<Pixmap> consumer;
    private int atx;
    private int aty;
    private int areawidth;
    private int areaheight;
    private Color color;
    
    public PixelPointLightTask(World world, Consumer<Pixmap> consumer, int tx, int ty, int areawidth, int areaheight,
            Color color, TileSystem tiles) {
        this.tiles = tiles;
        this.world = world;
        this.consumer = consumer;
        this.atx = tx;
        this.aty = ty;
        this.areawidth = areawidth;
        this.areaheight = areaheight;
        this.color = color;
    }
    
    private boolean checkBounds(int i, int j) {
        return i >= 0 && i < lightConstant * 2 + areawidth && j >= 0 && j < lightConstant * 2 + areaheight;
    }
    
    @Override
    public Void call() throws Exception {
        Pixmap pix = new Pixmap(areawidth + lightConstant * 2, areaheight + lightConstant * 2, Format.RGB888);
        pix.setColor(Color.BLACK);
        pix.fill();
        Queue<LightState> queue = new ArrayDeque<>();
        float[][] lightvalues = new float[pix.getWidth()][pix.getHeight()];
        for (int i = 0; i < areawidth; i++) {
            for (int j = 0; j < areaheight; j++) {
                int gtx = i + atx;
                int gty = j + aty;
                if (tiles.getTile(gtx, gty, TileLayer.Front) == Tile.NOTHING
                /* && head.r.getBackground(gtx, gty) == Tile.EMPTY */) {
                    LightState state = new LightState();
                    state.i = i + lightConstant;
                    state.j = j + lightConstant;
                    state.light = color;
                    state.value = 1;
                    state.index = 0;
                    lightvalues[state.i][state.j] = state.value;
                    queue.add(state);
                }
            }
        }
        Array<LightState> modify = new Array<>();
        while (!queue.isEmpty()) {
            LightState state = queue.poll();
            modify.add(state);
            if (state.value > threshold && state.index < lightConstant) {
                help(queue, state, state.i + 1, state.j, lightvalues);
                help(queue, state, state.i - 1, state.j, lightvalues);
                help(queue, state, state.i, state.j + 1, lightvalues);
                help(queue, state, state.i, state.j - 1, lightvalues);
            }
        }
        Color color = new Color();
        for (LightState l : modify) {
            color.set(l.light).mul(lightvalues[l.i][l.j]);
            pix.setColor(color);
            pix.drawPixel(l.i, pix.getHeight() - 1 - l.j);
        }
        Gdx.app.postRunnable(() -> {
            consumer.accept(pix);
            //pool.free(head);
        });
        return null;
    }
    
    private void help(Queue<LightState> queue, LightState front, int i, int j, float[][] intens) {
        if (!checkBounds(i, j)) {
            return;
        }
        int tx = atx + i - lightConstant;
        int ty = aty + j - lightConstant;
        Tile tile = Tile.NOTHING;//Hmmm...
        if (world.getBounds().inBounds(tx, ty)) {
            tile = tiles.getTile(tx, ty, TileLayer.Front);
        }
        
        float newIntens = Math.max(tile.getLightTransmission(), 0) * front.value;
        if (newIntens > intens[i][j]) {
            intens[i][j] = newIntens;
            LightState newState = new LightState();
            newState.i = i;
            newState.j = j;
            newState.value = newIntens;
            newState.light = front.light;
            newState.index = front.index + 1;
            queue.add(newState);
        }
        
    }
    
}
