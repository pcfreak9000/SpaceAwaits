package de.pcfreak9000.spaceawaits.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.render.SpriteBatchImpr;

public class Util {
    
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
        FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, w, h, false);
        SpriteBatchImpr batch = new SpriteBatchImpr(providers.length);
        Camera cam = new OrthographicCamera(1, -1);
        batch.setProjectionMatrix(cam.combined);
        fbo.begin();
        ScreenUtils.clear(0, 0, 0, 0);
        batch.setDefaultBlending();
        batch.begin();
        for (ITextureProvider tp : providers) {
            batch.draw(tp.getRegion(), -0.5f, -0.5f, 1, 1);
        }
        batch.end();
        Pixmap pix = Pixmap.createFromFrameBuffer(0, 0, w, h);
        fbo.end();
        Texture t = new Texture(pix);
        fbo.dispose();
        batch.dispose();
        pix.dispose();
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
}
