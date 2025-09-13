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

	private LevelTypeTiles lttiles = LevelTypeTiles.LTT;

	public Game(ISave save, ScreenManager scm) {
		this.mySave = save;
		this.scm = scm;
		this.screenCache = new SpecialCache<String, GameScreen>(5, 3, (uuid) -> {
			GameScreen screen = null;
			try {
				if (!this.mySave.hasLevel(uuid)) {
					LOGGER.error("No such level with uuid " + uuid);
					return null;
				}
				LOGGER.infof("Loading level...");
				ILevelSave lsave = this.mySave.getLevel(uuid);
				screen = lsave.getLevelType().createGameScreen(scm.getGuiHelper(), lsave.getWorldSave());
				screen.load();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			return screen;
		}, (screen) -> {
			Objects.requireNonNull(screen);
			if (this.gamescreenCurrent == screen) {
				leaveLevel();
			}
			screen.unload();
		});
	}

	public long getMasterSeed() {
		return mySave.getSaveMeta().getSeed();
	}

	public void loadGame() {
		this.player = new Player();
		this.readPlayer();
	}

	// probably also TMP
	//if the save doesn't have the current level, try to return to the original spawn?
	public void joinGame() {
		if (this.mySave.hasLevel(player.getCurrentLevel())) {
			this.joinLevel(player.getCurrentLevel());
		} else {
			// Check if this save has a spawn place, otherwise generate a new one
			// When generating a new world, place the player at spawn
			String uuid = createLevel(lttiles, new TilesLevelCreationVisitor(this.mySave.getSaveMeta().getSeed(),
					pickGenerator(Registry.GENERATOR_REGISTRY.getGens()), "Some world"));
			joinLevel(uuid);
		}
	}

	// TMP!!!!
	private IGeneratingLayer<WorldPrimer, GeneratorSettings> pickGenerator(
			Set<IGeneratingLayer<WorldPrimer, GeneratorSettings>> set) {
		List<IGeneratingLayer<WorldPrimer, GeneratorSettings>> list = new ArrayList<>();
		list.addAll(set);
		return MathUtil.getRandom(new Random(), list);
	}

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
