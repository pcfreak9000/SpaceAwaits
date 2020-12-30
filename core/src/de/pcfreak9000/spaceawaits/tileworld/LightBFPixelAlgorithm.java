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

import de.omnikryptec.math.Mathf;
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
        AsyncCalcInfo info = pool.obtain();
        info.r = region;
        info.consumer = createTexture;
        info.w = world;
        queue.add(info);
    }
    
    private static class LightSource {
        private Color color;
        private float[][] intensityField;
        private int gtx, gty;
        private int pgtx, pgty;
        private int lightConstant;
        private float initialIntensity;
        
        boolean checkBounds(int i, int j) {
            return i >= 0 && i < lightConstant * 2 + 1 && j >= 0 && j < lightConstant * 2 + 1;
        }
    }
    
    private static class LightState {
        private float value;
        private Color light = new Color();
        private int i, j;
        private LightSource source;
    }
    
    private Thread actualThread;
    
    public LightBFPixelAlgorithm() {
        actualThread = new Thread(threaded);
        actualThread.start();
    }
    
    private Runnable threaded = new Runnable() {
        final byte globalLightConstant = 10;
        
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
                    Queue<LightSource> queue2 = new ArrayDeque<>();
                    Pixmap pix = new Pixmap(Region.REGION_TILE_SIZE + globalLightConstant * 2,
                            Region.REGION_TILE_SIZE + globalLightConstant * 2, Format.RGB888);
                    Color[][] lightvalues = new Color[Region.REGION_TILE_SIZE
                            + globalLightConstant * 2][Region.REGION_TILE_SIZE + globalLightConstant * 2];
                    for (int i = 0; i < Region.REGION_TILE_SIZE; i++) {
                        for (int j = 0; j < Region.REGION_TILE_SIZE; j++) {
                            int gtx = i + head.r.getGlobalTileX();
                            int gty = j + head.r.getGlobalTileY();
                            if (head.r.getTile(gtx, gty) == Tile.EMPTY
                            /* && head.r.getBackground(gtx, gty) == Tile.EMPTY */) {
                                LightSource s = new LightSource();
                                s.color = Color.CORAL;
                                s.gtx = gtx;
                                s.gty = gty;
                                s.pgtx = gtx;
                                s.pgty = gty;
                                s.lightConstant = 10;
                                int l = s.lightConstant * 2 + 1;
                                s.intensityField = new float[l][l];
                                s.initialIntensity = 1;
                                queue2.add(s);
                            }
                        }
                    }
                    
                    while (!queue2.isEmpty()) {
                        LightSource source = queue2.poll();
                        LightState first = new LightState();
                        first.source = source;
                        first.i = source.lightConstant;
                        first.j = source.lightConstant;
                        first.value = source.initialIntensity;
                        first.light = source.color;
                        queue.add(first);
                        Array<LightState> modify = new Array<>();
                        while (!queue.isEmpty()) {
                            LightState state = queue.poll();
                            modify.add(state);//This way its incomplete!
                            help(head, queue, queue2, source, state, state.i + 1, state.j);
                            help(head, queue, queue2, source, state, state.i, state.j + 1);
                            help(head, queue, queue2, source, state, state.i - 1, state.j);
                            help(head, queue, queue2, source, state, state.i, state.j - 1);
                        }
                        for (LightState l : modify) {
                            int i = source.gtx  - head.r.getGlobalTileX() + l.i;
                            int j = source.gty  - head.r.getGlobalTileY() + l.j;
                            if (checkBounds(i, j)) {
                                if (lightvalues[i][j] == null) {
                                    lightvalues[i][j] = new Color(0,0,0,0);
                                }
                                lightvalues[i][j].add(l.light);
                            }
                        }
                    }
                    pix.setColor(Color.BLACK);
                    pix.fill();
                    for (int i = 0; i < pix.getWidth(); i++) {
                        for (int j = 0; j < pix.getHeight(); j++) {
                            if (lightvalues[i][j] != null) {
                                pix.setColor(lightvalues[i][j]);
                                pix.drawPixel(i, pix.getHeight() - 1 - j);
                            }
                        }
                    }
                    
                    //pix.setColor(l.light);
                    //pix.drawPixel(l.i, pix.getHeight() - 1 - l.j);
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
        
        private boolean isChromaInvariant(Color filter) {
            return filter.r == filter.g && filter.r == filter.b;
        }
        
        private float getColorMax(Color col) {
            return Mathf.max(col.r, Mathf.max(col.g, col.b));
        }
        
        private static final float THRESHOLD = 0.02f;//threshold constant...
        
        private void help(AsyncCalcInfo info, Queue<LightState> queue, Queue<LightSource> queue2, LightSource source,
                LightState front, int i, int j) {
            if (!source.checkBounds(i, j)) {
                return;
            }
            int tx = source.gtx - source.lightConstant + i;
            int ty = source.gty - source.lightConstant + j;
            if (tx == source.pgtx && ty == source.pgty) {
                return;//filtered light doesnt have a reverse mode
            }
            Tile tile = Tile.EMPTY;//Hmmm...
            if (info.w.getTileWorld().inBounds(tx, ty)) {
                tile = info.w.getTileWorld().getTile(tx, ty);
            }
            Color newlight = front.light.cpy().mul(tile.getFilterColor());
            if (isChromaInvariant(tile.getFilterColor())) {
                float newIntens = tile.getFilterColor().r * front.value;
                if (newIntens > source.intensityField[i][j]) {
                    source.intensityField[i][j] = newIntens;
                    if (newIntens > THRESHOLD) {
                        LightState newState = new LightState();
                        newState.source = source;
                        newState.i = i;
                        newState.j = j;//TODO local i and j vs gtx and gty
                        newState.value = newIntens;
                        newState.light = newlight;
                        queue.add(newState);
                    }
                }
            } else {
                float newMaxIntens = getColorMax(newlight);
                if (newMaxIntens > THRESHOLD) {
                    LightSource filteredLight = new LightSource();
                    filteredLight.color = newlight;
                    filteredLight.gtx = tx;
                    filteredLight.gty = ty;
                    filteredLight.pgtx = source.gtx - source.lightConstant + front.i;
                    filteredLight.pgty = source.gty - source.lightConstant + front.j;
                    filteredLight.initialIntensity = newMaxIntens;
                    int l = Mathf.ceili((source.lightConstant) * filteredLight.initialIntensity) * 2 + 1;
                    filteredLight.intensityField = new float[l][l];
                    queue2.add(filteredLight);
                }
            }
            
        }
    };
    
}
