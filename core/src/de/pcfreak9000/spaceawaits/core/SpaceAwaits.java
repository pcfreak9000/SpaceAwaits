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
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;
import de.pcfreak9000.spaceawaits.mod.Modloader;
import de.pcfreak9000.spaceawaits.save.SaveManager;
import de.pcfreak9000.spaceawaits.util.FileHandleClassLoaderExtension;
import de.pcfreak9000.spaceawaits.world.WorldManager;

public class SpaceAwaits extends Game {
    public static final boolean DEBUG = true;
    
    public static final String NAME = "Space Awaits";
    public static final String VERSION = "pre-Alpha-0";
    public static final AdvancedFile FOLDER = new AdvancedFile(OSUtil.getAppDataSubDirectory("." + NAME));
    public static final String RESOURCEPACKS = "resourcepacks";
    public static final String MODS = "mods";
    public static final String SAVES = "saves";
    
    public static final EventBus BUS = new EventBus();//Hmmm
    
    private static SpaceAwaits singleton;
    
    private static Logger LOGGER = Logger.getLogger(SpaceAwaits.class);
    
    public static SpaceAwaits getSpaceAwaits() {
        return singleton;
    }
    
    private Modloader modloader;
    private AssetManager assetManager;
    
    private GameManager gameManager; //Is this the correct place for that?
    
    private WorldManager worldManager;
    
    private ScreenManager screenManager;
    
    public SpaceAwaits() {
        if (SpaceAwaits.singleton != null) {
            throw new IllegalStateException("singleton violation");
        }
        SpaceAwaits.singleton = this;
    }
    
    @Override
    public void create() {
        //Setup debugging stuff
        Logger.setMinLogType(DEBUG ? LogType.Debug : LogType.Info);
        Gdx.app.setLogLevel(DEBUG ? Application.LOG_DEBUG : Application.LOG_INFO);
        
        //Instantiate stuff
        this.modloader = new Modloader();
        this.assetManager = createAssetmanager();
        this.worldManager = new WorldManager();
        AdvancedFile savesFolderFile = mkdirIfNotExisting(new AdvancedFile(FOLDER, SAVES));
        this.gameManager = new GameManager(new SaveManager(savesFolderFile.toFile()));
        this.screenManager = new ScreenManager(this);
        
        //Load mods and resources
        preloadResources();
        //setScreen(new LoadingScreen());
        //...
        this.modloader.load(mkdirIfNotExisting(new AdvancedFile(FOLDER, MODS)));
        CoreResources.init();
        LOGGER.info("Init...");
        BUS.post(new CoreEvents.InitEvent());
        LOGGER.info("Queue resources...");
        BUS.post(new CoreEvents.QueueResourcesEvent(assetManager));
        this.assetManager.finishLoading();
        LOGGER.info("Updating resources...");
        BUS.post(new CoreEvents.UpdateResourcesEvent(assetManager));
        LOGGER.info("Post-Init...");
        BUS.post(new CoreEvents.PostInitEvent());
        
        this.screenManager.setMainMenuScreen();
    }
    
    @Deprecated
    public WorldManager getWorldManager() {
        return this.worldManager;
    }
    
    public GameManager getGameManager() {
        return this.gameManager;
    }
    
    public ScreenManager getScreenManager() {
        return this.screenManager;
    }
    
    private void preloadResources() {//What happens on resource reload?
        this.assetManager.load("hyperraum.png", Texture.class);
        this.assetManager.load("text.fnt", BitmapFont.class);
        this.assetManager.load("missing_texture.png", Texture.class);
        //...
        this.assetManager.finishLoading();
    }
    
    public void exit() {
        Gdx.app.exit();
    }
    
    @Override
    public void dispose() {
        if (this.gameManager.getGameCurrent() != null) {//TODO this could be better, like some isIngame or smth
            LOGGER.warn("Unloading world (Exit while in loaded world)");
            this.gameManager.unloadGame();
        }
        LOGGER.info("Exit...");
        BUS.post(new CoreEvents.ExitEvent());
        super.dispose();
        this.screenManager.dispose();
        this.assetManager.dispose();
        LOGGER.info("Exit.");
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
    
    private AdvancedFile mkdirIfNotExisting(AdvancedFile file) {
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
