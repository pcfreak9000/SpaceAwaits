package de.pcfreak9000.spaceawaits.tileworld;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncTask;

import de.pcfreak9000.spaceawaits.tileworld.tile.Region;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class PixelPointLightTask implements AsyncTask<Void> {
    private static class LightState {
        private float value;
        private Color light = new Color();
        private int i, j;
        private int index;
    }
    
    private float threshold = 0.1f;
    private int lightConstant = 10;
    private World world;
    private Region region;
    private Consumer<Pixmap> consumer;
    
    public PixelPointLightTask(World world, Region region, Consumer<Pixmap> consumer) {
        this.world = world;
        this.region = region;
        this.consumer = consumer;
    }
    
    private boolean checkBounds(int i, int j) {
        return i >= 0 && i < lightConstant * 2 + Region.REGION_TILE_SIZE && j >= 0
                && j < lightConstant * 2 + Region.REGION_TILE_SIZE;
    }
    
    @Override
    public Void call() throws Exception {
        Queue<LightState> queue = new ArrayDeque<>();
        Pixmap pix = new Pixmap(Region.REGION_TILE_SIZE + lightConstant * 2,
                Region.REGION_TILE_SIZE + lightConstant * 2, Format.RGB888);
        pix.setColor(Color.BLACK);
        pix.fill();
        float[][] lightvalues = new float[Region.REGION_TILE_SIZE + lightConstant * 2][Region.REGION_TILE_SIZE
                + lightConstant * 2];
        for (int i = 0; i < Region.REGION_TILE_SIZE; i++) {
            for (int j = 0; j < Region.REGION_TILE_SIZE; j++) {
                int gtx = i + region.getGlobalTileX();
                int gty = j + region.getGlobalTileY();
                if (region.getTile(gtx, gty) == Tile.EMPTY
                /* && head.r.getBackground(gtx, gty) == Tile.EMPTY */) {
                    LightState state = new LightState();
                    state.i = i + lightConstant;
                    state.j = j + lightConstant;
                    state.light = Color.CORAL;
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
        int tx = region.getGlobalTileX() + i - lightConstant;
        int ty = region.getGlobalTileY() + j - lightConstant;
        Tile tile = Tile.EMPTY;//Hmmm...
        if (world.getTileWorld().inBounds(tx, ty)) {
            tile = world.getTileWorld().getTile(tx, ty);
        }
        
        float newIntens = Math.max(Math.min(tile.getLightLoss(), 0.99f), 0) * front.value;
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
