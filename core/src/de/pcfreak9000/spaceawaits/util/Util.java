package de.pcfreak9000.spaceawaits.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import de.omnikryptec.math.Mathf;

public class Util {
    
    public static void blitFramebuffer(FrameBuffer origin, FrameBuffer target, int originAttachment,
            int targetAttachment) {
        target.bind();
        GL30 gl = Gdx.gl30;
        gl.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, origin.getFramebufferHandle());
        gl.glReadBuffer(originAttachment);//Hmm
        gl.glBlitFramebuffer(0, 0, origin.getWidth(), origin.getHeight(), 0, 0, target.getWidth(), target.getHeight(),
                GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
        
    }
    
    public static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
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
