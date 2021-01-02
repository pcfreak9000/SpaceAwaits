package de.pcfreak9000.spaceawaits.core;

import java.util.List;
import java.util.Random;

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
import de.omnikryptec.math.MathUtil;
import de.pcfreak9000.spaceawaits.menu.MainMenuScreen;
import de.pcfreak9000.spaceawaits.mod.Modloader;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.tileworld.World;
import de.pcfreak9000.spaceawaits.tileworld.WorldGenerator;
import de.pcfreak9000.spaceawaits.tileworld.WorldGenerator.GeneratorCapabilitiesBase;
import de.pcfreak9000.spaceawaits.tileworld.WorldLoadingBounds;
import de.pcfreak9000.spaceawaits.tileworld.WorldManager;
import de.pcfreak9000.spaceawaits.tileworld.WorldScreen;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.util.FileHandleClassLoaderExtension;

public class SpaceAwaits extends Game {
    public static final boolean DEBUG = true;
    
    public static final String NAME = "Space Awaits";
    public static final String VERSION = "pre-Alpha-0";
    public static final AdvancedFile FOLDER = new AdvancedFile(OSUtil.getAppDataSubDirectory("." + NAME));
    public static final String RESOURCEPACKS = "resourcepacks";
    public static final String MODS = "mods";
    
    public static final EventBus BUS = new EventBus();//Hmmm
    
    private static SpaceAwaits singleton;
    
    public static SpaceAwaits getSpaceAwaits() {
        return singleton;
    }
    
    private Modloader modloader;
    public AssetManager assetManager;//TODO private, resource reloading in general
    private WorldManager worldManager;
    
    public WorldScreen worldScreen;
    public MainMenuScreen mainMenuScreen;
    
    public SpaceAwaits() {
        if (SpaceAwaits.singleton != null) {
            throw new IllegalStateException("singleton violation");
        }
        SpaceAwaits.singleton = this;
    }
    
    @Override
    public void create() {
        Gdx.app.setLogLevel(DEBUG ? Application.LOG_DEBUG : Application.LOG_INFO);
        this.modloader = new Modloader();
        this.assetManager = createAssetmanager();
        this.worldManager = new WorldManager();
        this.worldScreen = new WorldScreen(worldManager);
        this.mainMenuScreen = new MainMenuScreen();
        preloadResources();
        //setScreen(new LoadingScreen());
        //...
        this.modloader.load(mkdirIfNonExisting(new AdvancedFile(FOLDER, MODS)));
        GameRegistry.TILE_REGISTRY.reloadResources(assetManager);
        GameRegistry.BACKGROUND_REGISTRY.reloadResources(assetManager);
        this.assetManager.load("mensch.png", Texture.class);
        this.assetManager.finishLoading();
        GameRegistry.TILE_REGISTRY.setupTiles(assetManager);
        GameRegistry.BACKGROUND_REGISTRY.setupBackgroundss(assetManager);
        Player p = new Player();
        this.worldManager.getLoader().setWorldUpdateFence(
                new WorldLoadingBounds(p.getPlayerEntity().getComponent(TransformComponent.class).position));
        World testWorld = pickGenerator(GameRegistry.GENERATOR_REGISTRY.filtered(GeneratorCapabilitiesBase.LVL_ENTRY))
                .generateWorld(0);
        this.worldManager.getECSManager().addEntity(p.getPlayerEntity());
        this.worldManager.setWorld(testWorld);
        setScreen(mainMenuScreen);
    }
    
    public WorldManager getWorldManager() {
        return worldManager;
    }
    
    //TMP
    private WorldGenerator pickGenerator(List<WorldGenerator> list) {
        return MathUtil.getWeightedRandom(new Random(), list);
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
