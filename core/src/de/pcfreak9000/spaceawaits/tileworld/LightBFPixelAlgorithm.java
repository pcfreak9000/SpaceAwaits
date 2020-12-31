package de.pcfreak9000.spaceawaits.tileworld;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncExecutor;

import de.pcfreak9000.spaceawaits.tileworld.tile.Region;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class LightBFPixelAlgorithm {
    
    private ConcurrentLinkedQueue<AsyncCalcInfo> queue = new ConcurrentLinkedQueue<>();
    
    private static class AsyncCalcInfo implements Poolable {
        Region r;
        Consumer<Pixmap> consumer;
        World w;
        
        @Override
        public void reset() {
            this.r = null;
            this.consumer = null;
            this.w = null;
        }
    }
    
    private Pool<AsyncCalcInfo> pool = new Pool<AsyncCalcInfo>() {
        
        @Override
        protected AsyncCalcInfo newObject() {
            return new AsyncCalcInfo();
        }
        
    };
    
    public void submit(Region region, World world, Consumer<Pixmap> createTexture) {
        //        AsyncCalcInfo info = pool.obtain();
        //        info.r = region;
        //        info.consumer = createTexture;
        //        info.w = world;
        //        queue.add(info);
        executor.submit(new PixelPointLightTask(world, region, createTexture));
    }
    
    private static class LightState {
        private float value;
        private Color light = new Color();
        private int i, j;
        private int index;
    }
    
    private AsyncExecutor executor = new AsyncExecutor(4);
    
    private Thread actualThread;
    
    public LightBFPixelAlgorithm() {
        actualThread = new Thread(threaded);
        actualThread.start();
    }
    
    private Runnable threaded = new Runnable() {
        final int globalLightConstant = 10;
        private static final float THRESHOLD = 0.1f;//threshold constant...
        
        private boolean checkBounds(int i, int j) {
            return i >= 0 && i < globalLightConstant * 2 + Region.REGION_TILE_SIZE && j >= 0
                    && j < globalLightConstant * 2 + Region.REGION_TILE_SIZE;
        }
        
        @Override
        public void run() {
            while (true) {
                while (!queue.isEmpty()) {
                    AsyncCalcInfo head = queue.poll();
                    Queue<LightState> queue = new ArrayDeque<>();
                    Pixmap pix = new Pixmap(Region.REGION_TILE_SIZE + globalLightConstant * 2,
                            Region.REGION_TILE_SIZE + globalLightConstant * 2, Format.RGB888);
                    pix.setColor(Color.BLACK);
                    pix.fill();
                    float[][] lightvalues = new float[Region.REGION_TILE_SIZE
                            + globalLightConstant * 2][Region.REGION_TILE_SIZE + globalLightConstant * 2];
                    for (int i = 0; i < Region.REGION_TILE_SIZE; i++) {
                        for (int j = 0; j < Region.REGION_TILE_SIZE; j++) {
                            int gtx = i + head.r.getGlobalTileX();
                            int gty = j + head.r.getGlobalTileY();
                            if (head.r.getTile(gtx, gty) == Tile.EMPTY
                            /* && head.r.getBackground(gtx, gty) == Tile.EMPTY */) {
                                LightState state = new LightState();
                                state.i = i + globalLightConstant;
                                state.j = j + globalLightConstant;
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
                        if (state.value > THRESHOLD && state.index < globalLightConstant) {
                            help(head, queue, state, state.i + 1, state.j, lightvalues);
                            help(head, queue, state, state.i - 1, state.j, lightvalues);
                            help(head, queue, state, state.i, state.j + 1, lightvalues);
                            help(head, queue, state, state.i, state.j - 1, lightvalues);
                        }
                    }
                    Color color = new Color();
                    for (LightState l : modify) {
                        color.set(l.light).mul(lightvalues[l.i][l.j]);
                        pix.setColor(color);
                        pix.drawPixel(l.i, pix.getHeight() - 1 - l.j);
                    }
                    Gdx.app.postRunnable(() -> {
                        head.consumer.accept(pix);
                        pool.free(head);
                    });
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void help(AsyncCalcInfo info, Queue<LightState> queue, LightState front, int i, int j,
                float[][] intens) {
            if (!checkBounds(i, j)) {
                return;
            }
            int tx = info.r.getGlobalTileX() + i - globalLightConstant;
            int ty = info.r.getGlobalTileY() + j - globalLightConstant;
            Tile tile = Tile.EMPTY;//Hmmm...
            if (info.w.getTileWorld().inBounds(tx, ty)) {
                tile = info.w.getTileWorld().getTile(tx, ty);
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
    };
    
}
