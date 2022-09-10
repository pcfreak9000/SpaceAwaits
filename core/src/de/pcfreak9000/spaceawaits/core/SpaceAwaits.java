package de.pcfreak9000.spaceawaits.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;

import de.codemakers.base.os.OSUtil;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;
import de.pcfreak9000.spaceawaits.generation.Generation;
import de.pcfreak9000.spaceawaits.mod.Modloader;
import de.pcfreak9000.spaceawaits.save.SaveManager;
import de.pcfreak9000.spaceawaits.screen.ScreenManager;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;
import de.pcfreak9000.spaceawaits.util.FileHandleClassLoaderExtension;
import de.pcfreak9000.spaceawaits.world.WorldSetupHandler;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;

public class SpaceAwaits extends Game {
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_CAMERA = false;
    
    public static final String NAME = "Space Awaits";
    public static final String VERSION = "pre-Alpha-0";
    public static final AdvancedFile FOLDER = new AdvancedFile(OSUtil.getAppDataSubDirectory("." + NAME));
    public static final String RESOURCEPACKS = "resourcepacks";
    public static final String MODS = "mods";
    public static final String SAVES = "saves";
    
    public static final EventBus BUS = new EventBus();//Hmmm
    
    private static SpaceAwaits singleton;
    
    private static final Logger LOGGER = Logger.getLogger(SpaceAwaits.class);
    
    public static SpaceAwaits getSpaceAwaits() {
        return singleton;
    }
    
    private Modloader modloader;
    private AssetManager assetManager;
    
    private GameManager gameManager; //Is this the correct place for that?
    
    private ScreenManager screenManager;
    
    private Set<Class<?>> classesWithSerialize = new LinkedHashSet<>();
    private Set<Class<?>> classesWithSerializeIm = Collections.unmodifiableSet(classesWithSerialize);
    private Set<Class<? extends Component>> componentsWithSerialize = new LinkedHashSet<>();
    private Set<Class<? extends Component>> componentsWithSerializeIm = Collections
            .unmodifiableSet(componentsWithSerialize);
    private Map<String, Class<? extends Component>> componentsByKey = new HashMap<>();
    
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
        Generation.test();
        //Instantiate infrastructure
        this.modloader = new Modloader();
        this.assetManager = createAssetmanager();
        AdvancedFile savesFolderFile = mkdirIfNotExisting(new AdvancedFile(FOLDER, SAVES));
        
        //Load mods and resources
        preloadResources();
        setupTooltipManager();
        //setScreen(new LoadingScreen());
        //...
        this.modloader.load(mkdirIfNotExisting(new AdvancedFile(FOLDER, MODS)));
        doReflectionStuff();
        LOGGER.info("Init...");
        CoreRes.init();
        Components.registerComponents();
        BUS.post(new CoreEvents.InitEvent());
        LOGGER.info("Queue resources...");
        BUS.post(new CoreEvents.QueueResourcesEvent(assetManager));
        this.assetManager.finishLoading();
        LOGGER.info("Updating resources...");
        BUS.post(new CoreEvents.UpdateResourcesEvent(assetManager));
        LOGGER.info("Post-Init...");
        BUS.post(new CoreEvents.PostInitEvent());
        //Where to put this?
        BUS.register(new WorldSetupHandler());
        //Instantiate game stuff
        this.screenManager = new ScreenManager(this);
        this.gameManager = new GameManager(new SaveManager(savesFolderFile.toFile()),
                this.screenManager.getGameRenderer());
        
        this.screenManager.setMainMenuScreen();
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
    
    private void setupTooltipManager() {
        TooltipManager mgr = TooltipManager.getInstance();
        mgr.offsetX = 0.15f;
        mgr.offsetY = 0.15f;
        mgr.animations = false;
    }
    
    public void exit() {
        Gdx.app.exit();
    }
    
    @Override
    public void dispose() {
        if (this.gameManager.isInGame()) {
            LOGGER.warn("Unloading world (Exit while in loaded world)");
            this.gameManager.unloadGame();
        }
        LOGGER.info("Exit...");
        BUS.post(new CoreEvents.ExitEvent());
        super.dispose();
        this.screenManager.dispose();
        this.assetManager.dispose();
        CoreRes.dispose();
        LOGGER.info("Exit.");
    }
    
    private void doReflectionStuff() {
        Reflections refl = new Reflections("de.pcfreak9000.spaceawaits");
        classesWithSerialize.addAll(refl.getTypesAnnotatedWith(NBTSerialize.class));
        classesWithSerialize.addAll(this.modloader.getModClassesWithSerialize());
        for (Class<?> c : classesWithSerialize) {
            if (Component.class.isAssignableFrom(c)) {
                componentsWithSerialize.add((Class<? extends Component>) c);
                componentsByKey.put(c.getAnnotation(NBTSerialize.class).key(), (Class<? extends Component>) c);
            }
        }
    }
    
    public Set<Class<?>> getClassesWithSerialize() {
        return classesWithSerializeIm;
    }
    
    public Set<Class<? extends Component>> getComponentsWithSerialize() {
        return componentsWithSerializeIm;
    }
    
    public Class<? extends Component> getComponentByKey(String key) {
        return componentsByKey.get(key);
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
