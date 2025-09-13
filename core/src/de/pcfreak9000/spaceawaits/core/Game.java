package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.utils.ObjectMap;

import de.omnikryptec.math.MathUtil;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.ScreenManager;
import de.pcfreak9000.spaceawaits.core.screen.TileScreen;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.save.ILevelSave;
import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.save.LevelCreationVisitor;
import de.pcfreak9000.spaceawaits.save.LevelType;
import de.pcfreak9000.spaceawaits.save.LevelTypeTiles;
import de.pcfreak9000.spaceawaits.save.TilesLevelCreationVisitor;
import de.pcfreak9000.spaceawaits.save.WorldMeta;
import de.pcfreak9000.spaceawaits.save.regionfile.RegionFileCache;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.util.SpecialCache;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pottgames.tuningfork.misc.Objects;

public class Game {

	private static final Logger LOGGER = Logger.getLogger(Game.class);

	// switch worlds etc

	private ISave mySave;
	private ScreenManager scm;// Meh?

	private Player player;
	private GameScreen gamescreenCurrent;

	private SpecialCache<String, GameScreen> screenCache;
	// private ObjectMap<String, GameScreen> screenCache = new ObjectMap<>();

	private LevelTypeTiles lttiles = LevelTypeTiles.LTT;

	public Game(ISave save, ScreenManager scm) {
		this.mySave = save;
		this.scm = scm;
		this.screenCache = new SpecialCache<String, GameScreen>(5, 3, (key) -> loadLevel(key), (gs) -> unloadLevel(gs));
	}

	public long getMasterSeed() {
		return mySave.getSaveMeta().getSeed();
	}

	public void loadGame() {
		this.player = new Player();
		this.readPlayer();
	}

//	// Why does this not simply use saveGame()?
//	public void unloadGame() {
//		LOGGER.info("Unloading...");
//		saveAndLeaveCurrentWorld();
//		writePlayer();
//	}
//
//	public void saveGame() {
//		LOGGER.info("Saving...");
//		gamescreenCurrent.save();
//		writePlayer();
//	}

	// probably also TMP
	public void joinGame() {
		if (this.mySave.hasLevel(player.getCurrentLevel())) {
			this.joinLevel(player.getCurrentLevel());
		} else {
			// Check if this save has a spawn place, otherwise generate a new one
			// When generating a new world, place the player at spawn
//			String id = createWorld("A nice World", pickGenerator(Registry.GENERATOR_REGISTRY.getGens()),
//					this.mySave.getSaveMeta().getSeed());// TODO Derive world seed from that master seed instead of
//															// using it directly
//			joinWorld(id);
			String uuid = createLevel(lttiles, new TilesLevelCreationVisitor(this.mySave.getSaveMeta().getSeed(),
					pickGenerator(Registry.GENERATOR_REGISTRY.getGens()), "Some world"));
			joinLevel(uuid);
		}
//        FlatScreen flatscreen = new FlatScreen(scm.getGuiHelper());
//        this.gamescreenCurrent = flatscreen;
//        flatscreen.load();
//        scm.setGameScreen(flatscreen);
//        flatscreen.join(player);
	}

	// TMP!!!!
	private IGeneratingLayer<WorldPrimer, GeneratorSettings> pickGenerator(
			Set<IGeneratingLayer<WorldPrimer, GeneratorSettings>> set) {
		List<IGeneratingLayer<WorldPrimer, GeneratorSettings>> list = new ArrayList<>();
		list.addAll(set);
		return MathUtil.getRandom(new Random(), list);
	}

//	public void saveAndLeaveCurrentWorld() {
//		player.leave(this.gamescreenCurrent);
//		this.gamescreenCurrent.unload();
//		this.gamescreenCurrent = null;
//	}

	public void unloadGame() {
		screenCache.clear();
	}

	public void saveGame() {
		for (GameScreen gs : screenCache.values()) {
			gs.save();
		}
		writePlayer();
	}

	public void joinLevel(String uuid) {
		this.gamescreenCurrent = screenCache.getOrFresh(uuid);
		LOGGER.info("Joining level...");
		this.scm.setGameScreen(this.gamescreenCurrent);
		this.player.join(this.gamescreenCurrent, uuid);
	}

	public void leaveLevel() {
		this.player.leave(gamescreenCurrent);
		this.scm.setGameScreen(null);
		this.gamescreenCurrent = null;
	}

	private void unloadLevel(GameScreen screen) {
		Objects.requireNonNull(screen);
		if (this.gamescreenCurrent == screen) {
			leaveLevel();
		}
		screen.unload();
	}

	private GameScreen loadLevel(String uuid) {
		GameScreen screen = null;
		try {
			if (!this.mySave.hasLevel(uuid)) {
				LOGGER.error("No such level with uuid " + uuid);
				return null;
			}
			LOGGER.infof("Loading level...");
			ILevelSave save = this.mySave.getLevel(uuid);
			screen = save.getLevelType().createGameScreen(scm.getGuiHelper(), save.getWorldSave());
			screen.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return screen;
	}

	public String createLevel(LevelType type, LevelCreationVisitor visitor) {
		String uuid = this.mySave.createLevel(type);
		try {
			ILevelSave save = this.mySave.getLevel(uuid);
			type.initializeLevel(visitor, save);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uuid;
	}

//	public void joinWorld(String uuid) {
//		try {
//			if (!this.mySave.hasWorld(uuid)) {
//				LOGGER.error("No such world with uuid: " + uuid);
//				return;
//			}
//			LOGGER.infof("Setting up world for joining...");
//			IWorldSave save = this.mySave.getWorld(uuid);
//			TileScreen tilescreen = new TileScreen(scm.getGuiHelper(), save);
//			this.gamescreenCurrent = tilescreen;
//
//			tilescreen.load();
//
//			LOGGER.info("Joining world...");
//			scm.setGameScreen(tilescreen);
//			player.join(tilescreen);
//
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

//	public String createWorld(String name, IGeneratingLayer<WorldPrimer, GeneratorSettings> generator, long seed) {
//		LOGGER.infof("Creating world...");
//		WorldPrimer worldPrimer = generator.generate(new GeneratorSettings(seed));
//		WorldMeta wMeta = WorldMeta.builder().displayName(name).worldSeed(seed).createdNow()
//				.worldGenerator(Registry.GENERATOR_REGISTRY.getId(generator)).dimensions(worldPrimer.getWorldBounds())
//				.create();
//		// The meta can probably be cached (useful for create-and-join)
//		// See Save.java for todo on uuid in meta...?
//		try {
//			String uuid = this.mySave.createWorld(wMeta);
//			return uuid;
//		} catch (IOException ex) {
//			throw new RuntimeException(ex);
//		}
//	}

	public GameScreen getGameScreenCurrent() {
		return this.gamescreenCurrent;
	}

	public Player getPlayer() {
		return this.player;
	}

	private void readPlayer() {
		if (this.mySave.hasPlayer()) {
			NBTCompound savestate = mySave.readPlayerNBT();
			this.player.readNBT(savestate.getCompound("player"));
		}
	}

	private void writePlayer() {
		NBTCompound comp = new NBTCompound();
		comp.put("player", INBTSerializable.writeNBT(player));
		mySave.writePlayerNBT(comp);
	}

}
