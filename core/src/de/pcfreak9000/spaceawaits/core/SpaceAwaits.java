package de.pcfreak9000.spaceawaits.core;

import java.util.Arrays;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;

import de.codemakers.base.os.OSUtil;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.screen.ScreenManager;
import de.pcfreak9000.spaceawaits.mod.Modloader;
import de.pcfreak9000.spaceawaits.save.SaveManager;
import de.pcfreak9000.spaceawaits.util.FileHandleClassLoaderExtension;
import de.pcfreak9000.spaceawaits.world.WorldSetupHandler;
import de.pottgames.tuningfork.Audio;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.AudioDevice;
import de.pottgames.tuningfork.AudioDeviceConfig;
import de.pottgames.tuningfork.StreamedSoundSource;
import de.pottgames.tuningfork.jukebox.JukeBox;
import de.pottgames.tuningfork.jukebox.playlist.PlayList;
import de.pottgames.tuningfork.jukebox.playlist.PlayListProvider;
import de.pottgames.tuningfork.jukebox.song.Song;
import de.pottgames.tuningfork.jukebox.song.SongSettings;

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
    
    private static final Logger LOGGER = Logger.getLogger(SpaceAwaits.class);
    
    public static SpaceAwaits getSpaceAwaits() {
        return singleton;
    }
    
    //Infrastructure
    private Audio audio;
    private Modloader modloader;
    private AssetManager assetManager;
    private FileHandleResolver filehandleresolver;
    private SaveManager saveManager;
    private ScreenManager screenManager;
    private ReflectionManager reflectionManager;
    private GameManager gameManager;
    
    private JukeBox jukebox;
    private PlayList playlist;
    
    public SpaceAwaits() {
        if (SpaceAwaits.singleton != null) {
            throw new IllegalStateException("singleton violation");
        }
        Engine.debug = DEBUG;
        Logger.setMinLogType(DEBUG ? LogType.Debug : LogType.Info);
        SpaceAwaits.singleton = this;
    }
    
    @Override
    public void create() {
        Gdx.app.setLogLevel(DEBUG ? Application.LOG_DEBUG : Application.LOG_INFO);
        //Instantiate infrastructure
        setupInfrastructure();
        //Preload resources required for a loading screen
        preloadResources();
        //setScreen(new LoadingScreen());
        loadGame();
        
        //Where to put this?
        BUS.register(new WorldSetupHandler());
        StreamedSoundSource songsrc = new StreamedSoundSource(Gdx.files.internal("ObservingTheStar.mp3"));
        songsrc.setRelative(true);
        SongSettings settings = SongSettings.linear(0.5f, 2f, 2f);
        Song song = new Song(songsrc, settings);
        this.playlist = new PlayList();
        playlist.addSong(song);
        this.jukebox = new JukeBox(new PlayListProvider() {
            
            @Override
            public PlayList next() {
                return playlist;
            }
            
            @Override
            public boolean hasNext() {
                return true;
            }
        });
        jukebox.play();
        //this.setScreen(new TestScreen());
        
        this.screenManager.setMainMenuScreen();
    }
    
    @Override
    public void render() {
        super.render();
        this.jukebox.update();
    }
    
    private void loadGame() {
        this.modloader.load(mkdirIfNotExisting(new AdvancedFile(FOLDER, MODS)));
        LOGGER.info("Init...");
        this.reflectionManager.collectAnnotationsEtc();
        CoreRes.init();
        BUS.post(new CoreEvents.InitEvent());
        LOGGER.info("Queue resources...");
        BUS.post(new CoreEvents.QueueResourcesEvent(assetManager));
        this.assetManager.finishLoading();
        LOGGER.info("Updating resources...");
        BUS.post(new CoreEvents.UpdateResourcesEvent(assetManager));
        LOGGER.info("Post-Init...");
        BUS.post(new CoreEvents.PostInitEvent());
    }
    
    private void setupInfrastructure() {
        audio = Audio.init();
        if (audio == null) {
            throw new RuntimeException("No audio");
        }
        initFileHandleResolver();
        this.modloader = new Modloader();
        createAssetmanager();
        setupTooltipManager();
        AdvancedFile savesFolderFile = mkdirIfNotExisting(new AdvancedFile(FOLDER, SAVES));
        this.saveManager = new SaveManager(savesFolderFile.toFile());
        this.screenManager = new ScreenManager(this);
        this.reflectionManager = new ReflectionManager(modloader);
        this.gameManager = new GameManager(saveManager, this.screenManager);
    }
    
    private void initFileHandleResolver() {
        this.filehandleresolver = new FileHandleResolver() {
            @Override
            public FileHandle resolve(String fileName) {
                return new FileHandleClassLoaderExtension(fileName, modloader.getModClassLoader());
            }
        };
    }
    
    public FileHandleResolver getFileHandleResolver() {
        return filehandleresolver;
    }
    
    public ReflectionManager getReflectionManager() {
        return this.reflectionManager;
    }
    
    public GameManager getGameManager() {
        return this.gameManager;
    }
    
    public ScreenManager getScreenManager() {
        return this.screenManager;
    }
    
    private void preloadResources() {//What happens on resource reload?
        //this.assetManager.load("text.fnt", BitmapFont.class);
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
        this.setScreen(null);
        super.dispose();
        this.assetManager.dispose();
        CoreRes.dispose();
        this.audio.dispose();
        LOGGER.info("Exit.");
        Gdx.gl.glDepthMask(false);//not doing this causes a SIGBUS on glfw window destruction on my system for whatever reason
    }
    
    private void createAssetmanager() {
        AssetManager manager = new AssetManager(filehandleresolver);
        manager.setLoader(Skin.class, new SkinLoaderModified(manager.getFileHandleResolver()));
        audio.registerAssetManagerLoaders(manager);
        this.assetManager = manager;
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
