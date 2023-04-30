package de.pcfreak9000.spaceawaits.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

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
        config.useOpenGL3(true, 3, 3);
        config.useVsync(!SpaceAwaits.DEBUG);
        config.setTitle(SpaceAwaits.NAME + " " + SpaceAwaits.VERSION);
        config.enableGLDebugOutput(SpaceAwaits.DEBUG, System.out);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        new Lwjgl3Application(instance = new SpaceAwaits(), config);
    }
    
}
