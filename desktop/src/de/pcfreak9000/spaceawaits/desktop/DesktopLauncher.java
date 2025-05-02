package de.pcfreak9000.spaceawaits.desktop;

import static org.lwjgl.openal.EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER;

import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EnumerateAllExt;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALUtils;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class DesktopLauncher {
    
    private static SpaceAwaits instance;
    
    public static void main(String[] arg) {
        //        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
        //            e.printStackTrace();
        //            if (instance != null) {
        //                instance.dispose();
        //            }
        //        });
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        //config.useOpenGL3(true, 3, 3);
        config.setOpenGLEmulation(GLEmulation.GL20, 3, 3);//GL30 somehow creates errors with the texture of the default BitmapFont
        config.useVsync(!SpaceAwaits.DEBUG);
        config.setTitle(SpaceAwaits.NAME + " " + SpaceAwaits.VERSION);
        config.enableGLDebugOutput(SpaceAwaits.DEBUG, System.out);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        config.disableAudio(false);
        new Lwjgl3Application(instance = new SpaceAwaits(), config);
    }
    
}
