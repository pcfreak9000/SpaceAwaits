package de.pcfreak9000.spaceawaits.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.composer.Recorder;
import de.pcfreak9000.spaceawaits.core.assets.ITextureProvider;
import de.pcfreak9000.spaceawaits.generation.IGen1D;
import de.pcfreak9000.spaceawaits.generation.IGenDouble2D;
import de.pcfreak9000.spaceawaits.generation.IGenInt2D;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.render.SpriteBatchImpr;

public class Util {
    
    public static final Interpolation INTERPOL_NONE = new Interpolation() {
        
        @Override
        public float apply(float a) {
            return a > 0.5f ? 1f : 0f;
        }
    };
    
    @Deprecated
    public static float interpolateStepwise2Ddirect(int x, int y, IGenInt2D stepwise, Interpolation interpolinterpol,
            int interpconstmax) {
        if (interpconstmax == 0) {
            return stepwise.generate(x, y);
        }
        int radius = interpconstmax;
        float cumIntVal = 0;
        float value = 0;
        for (int iy = -radius; iy <= radius; iy++) {
            int dx = (int) Math.sqrt(radius * radius - iy * iy);
            for (int ix = -dx; ix <= dx; ix++) {
                double radCoord = Math.sqrt(ix * ix + iy * iy);
                float interpolValue = interpolinterpol.apply((float) (1 - (radCoord / (double) radius)));
                cumIntVal += interpolValue;
                value += stepwise.generate(x + ix, y + iy) * interpolValue;
            }
        }
        return value / cumIntVal;
    }
    
    public static <T extends IStepWiseComponent> float interpolateStepwise(int x, IGen1D<T> stepwise,
            IPropertyGetter<T> tointerpolate, Interpolation interpolinterpol, int interpconstmax) {
        if (interpconstmax == 0) {
            return tointerpolate.getValue(x, stepwise.generate(x));
        }
        int interpconstleft = stepwise.generate(x + interpconstmax).getInterpolationDistance();
        int interpconstright = stepwise.generate(x - interpconstmax - 1).getInterpolationDistance();
        interpconstleft = Math.min(interpconstleft, interpconstmax);
        interpconstright = Math.min(interpconstright, interpconstmax);
        float tx = tointerpolate.getValue(x, stepwise.generate(x));
        float txd0 = tointerpolate.getValue(x + interpconstright, stepwise.generate(x + interpconstright));
        float txd1 = tointerpolate.getValue(x - interpconstleft - 1, stepwise.generate(x - interpconstleft - 1));
        if (tx != txd0) {
            //x is on the left of some step
            //we need to find where the step occurs
            //we start at x and find the distance to the step
            double runv = tx;
            int intx = x;
            while (runv == tx) {//Use some binary search here instead?
                intx++;
                runv = tointerpolate.getValue(intx, stepwise.generate(intx));
            }
            return InterpolationInterpolation.apply(tx, txd0,
                    (x - (intx - interpconstright)) / (float) (interpconstleft + interpconstright),
                    stepwise.generate(x).getInterpolation(), stepwise.generate(x + interpconstright).getInterpolation(),
                    interpolinterpol);
        } else if (tx != txd1) {
            //x is on the right of some step
            //we need to find where the step occurs
            //we start at x and find the distance to the step, but reversed
            double runv = txd1;
            int intx = x - interpconstleft - 1;
            while (runv != tx) {//Use some binary search here instead?
                intx++;
                runv = tointerpolate.getValue(intx, stepwise.generate(intx));
            }
            return InterpolationInterpolation.apply(txd1, tx,
                    (x - (intx - interpconstright)) / (float) (interpconstleft + interpconstright),
                    stepwise.generate(x - interpconstleft - 1).getInterpolation(),
                    stepwise.generate(x).getInterpolation(), interpolinterpol);
        }
        return tx;
    }
    
    public static int interpolate(float x, float y, int[][] array, Interpolation interpolator) {
        int x0 = Mathf.floori(x);
        int y0 = Mathf.floori(y);
        if (MathUtils.isEqual(x, x0) && MathUtils.isEqual(y, y0)) {
            return array[x0][y0];
        }
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        float xs = interpolator.apply(x - x0);
        float ys = interpolator.apply(y - y0);
        return Math.round(interpolateXY2(x, y, xs, ys, x0, x1, y0, y1, array));
    }
    
    private static float interpolateX2(float x, float y, float xs, int x0, int x1, int iy, int[][] array) {
        float v1 = array[x0][iy];
        float v2 = array[x1][iy];
        return MathUtils.lerp(v1, v2, xs);
    }
    
    public static float interpolateXY2(float x, float y, float xs, float ys, int x0, int x1, int y0, int y1,
            int[][] array) {
        float v1 = interpolateX2(x, y, xs, x0, x1, y0, array);
        float v2 = interpolateX2(x, y, xs, x0, x1, y1, array);
        return MathUtils.lerp(v1, v2, ys);
    }
    
    public static boolean checkChunkInFrustum(Chunk chunk, Camera camera) {
        float mx = (chunk.getGlobalChunkX() + 0.5f) * Chunk.CHUNK_SIZE;
        float my = (chunk.getGlobalChunkY() + 0.5f) * Chunk.CHUNK_SIZE;
        return camera.frustum.boundsInFrustum(mx, my, 0, 0.5f * Chunk.CHUNK_SIZE, 0.5f * Chunk.CHUNK_SIZE, 0);
    }
    
    //TODO move to Composer and improve that class, or something... probably as a subclass
    public static Texture combine(ITextureProvider... providers) {
        int w = 0, h = 0;
        for (ITextureProvider tp : providers) {
            w = Math.max(w, tp.getRegion().getRegionWidth());
            h = Math.max(h, tp.getRegion().getRegionHeight());
        }
        Recorder rec = new Recorder(w, h);
        SpriteBatchImpr batch = new SpriteBatchImpr(providers.length);
        Camera cam = new OrthographicCamera(1, -1);
        batch.setProjectionMatrix(cam.combined);
        rec.begin();
        batch.setDefaultBlending();
        batch.begin();
        for (ITextureProvider tp : providers) {
            batch.draw(tp.getRegion(), -0.5f, -0.5f, 1, 1);
        }
        batch.end();
        Texture t = rec.end();
        batch.dispose();
        rec.dispose();
        return t;
    }
    
    private static final double TEMPERATURE_RED_EXP_CONST = 329.698727446;
    private static final double TEMPERATURE_RED_EXP = -0.1332047592;
    private static final double TEMPERATURE_GREEN_LN_CONST = 99.4708025861;
    private static final double TEMPERATURE_GREEN_LN_SUB = 161.1195681661;
    private static final double TEMPERATURE_GREEN_EXP_CONST = 288.1221695283;
    private static final double TEMPERATURE_GREEN_EXP = -0.0755148492;
    private static final double TEMPERATURE_BLUE_LN_CONST = 138.5177312231;
    private static final double TEMPERATURE_BLUE_LN_SUB = 305.0447927307;
    
    /**
     * Converts a color temperature into a {@link Color}. The color temperature
     * should be in the range [0, 50000].
     *
     * @param colTemperature the color temperature in kelvin 50000]
     * @return the converted Color
     */
    public static Color ofTemperature(float colTemperature) {
        colTemperature = MathUtils.clamp(colTemperature, 0, 50000);
        float red = 0;
        float green = 0;
        float blue = 0;
        colTemperature /= 100.0f;
        if (colTemperature <= 66.0f) {
            red = 255;
        } else {
            red = colTemperature - 60.0f;
            red = (float) (TEMPERATURE_RED_EXP_CONST * Math.pow(red, TEMPERATURE_RED_EXP));
        }
        if (colTemperature <= 66.0f) {
            green = colTemperature;
            green = (float) (TEMPERATURE_GREEN_LN_CONST * Math.log(green) - TEMPERATURE_GREEN_LN_SUB);
        } else {
            green = colTemperature - 60.0f;
            green = (float) (TEMPERATURE_GREEN_EXP_CONST * Math.pow(green, TEMPERATURE_GREEN_EXP));
        }
        if (colTemperature >= 66.0f) {
            blue = 255.0f;
        } else if (colTemperature <= 19.0f) {
            blue = 0.0f;
        } else {
            blue = colTemperature - 10.0f;
            blue = (float) (TEMPERATURE_BLUE_LN_CONST * Math.log(blue) - TEMPERATURE_BLUE_LN_SUB);
        }
        red = Mathf.clamp(red, 0.0f, 255.0f);
        green = Mathf.clamp(green, 0.0f, 255.0f);
        blue = Mathf.clamp(blue, 0.0f, 255.0f);
        return new Color(red / 255.0f, green / 255.0f, blue / 255.0f, 1f);
    }
    
    public static int[][] smoothCA(IGenDouble2D src, int x, int y, int width, int height, Direction[] rule,
            int minSolidCount, int iterations, double thresh) {
        //Module noise = noiseGen.get();
        int[][] result = new int[width][height];
        if (iterations == 0) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i][j] = src.generate((x + i), (y + j)) >= thresh ? 1 : 0;
                }
            }
            return result;
        }
        int caWidth = width + (iterations - 1) * 2;
        int caHeight = height + (iterations - 1) * 2;
        int[][] read = new int[caWidth][caHeight];
        int[][] write = new int[caWidth][caHeight];
        for (int i = 0; i < iterations; i++) {
            for (int j = i; j < caWidth - i; j++) {
                for (int k = i; k < caHeight - i; k++) {
                    
                    int count = 0;
                    for (Direction d : rule) {
                        if (i == 0) {
                            if (src.generate((x + j + d.dx), (y + k + d.dy)) >= thresh) {
                                count++;
                                write[j][k] = 1;
                                //read[j][k] = 1;
                            }
                        } else {
                            if (read[j + d.dx][k + d.dy] == 1) {
                                count++;
                            }
                        }
                    }
                    
                    if (count > minSolidCount) {
                        write[j][k] = 1;
                    } else if (count < minSolidCount) {
                        write[j][k] = 0;
                    } else {
                        write[j][k] = read[j][k];
                    }
                    if (i == iterations - 1) {
                        result[j - iterations + 1][k - iterations + 1] = write[j][k];
                    }
                    
                }
            }
            int[][] tmp = read;
            read = write;
            write = tmp;
        }
        
        return result;
    }
    
}
