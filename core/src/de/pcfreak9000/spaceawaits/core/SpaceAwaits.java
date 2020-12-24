package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.codemakers.base.os.OSUtil;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.mod.Modloader;
import de.pcfreak9000.spaceawaits.util.FileHandleClassLoaderExtension;

public class SpaceAwaits extends Game {
    public static final boolean DEBUG = true;
    
    public static final String NAME = "Space Awaits";
    public static final String VERSION = "pre-Alpha-0";
    public static final AdvancedFile FOLDER = new AdvancedFile(OSUtil.getAppDataSubDirectory("." + NAME));
    public static final String RESOURCEPACKS = "resourcepacks";
    public static final String MODS = "mods";
    
    public static final EventBus BUS = new EventBus();//Hmmm
    
    private Modloader modloader;
    private AssetManager assetManager;
    
    @Override
    public void create() {
        Gdx.app.setLogLevel(DEBUG ? Application.LOG_DEBUG : Application.LOG_INFO);
        this.modloader = new Modloader();
        this.assetManager = createAssetmanager();
        preloadResources();
        //setScreen(new LoadingScreen());
        //...
        this.modloader.load(mkdirIfNonExisting(new AdvancedFile(FOLDER, MODS)));
    }
    
    private void preloadResources() {
        this.assetManager.load("hyperraum.png", Texture.class);
        this.assetManager.load("text.fnt", BitmapFont.class);
        //...
        this.assetManager.finishLoading();
    }
    
    private AssetManager createAssetmanager() {
        AssetManager manager = new AssetManager(new FileHandleResolver() {
            @Override
            public FileHandle resolve(String fileName) {
                return new FileHandleClassLoaderExtension(fileName, modloader.getModClassLoader());
            }
        });
        return manager;
    }
    
    private AdvancedFile mkdirIfNonExisting(AdvancedFile file) {
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
