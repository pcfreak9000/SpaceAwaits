package de.pcfreak9000.spaceawaits.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.MathUtil;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.ScreenManager;
import de.pcfreak9000.spaceawaits.core.screen.TileScreen;
import de.pcfreak9000.spaceawaits.flat.FlatScreen;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.save.ISave;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.save.WorldMeta;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;

public class Game {

    private static final Logger LOGGER = Logger.getLogger(Game.class);

    // switch worlds etc

    private ISave mySave;
    private ScreenManager scm;// Meh?

    private Player player;
    private GameScreen gamescreenCurrent;

    public Game(ISave save, ScreenManager scm) {
        this.mySave = save;
        this.scm = scm;
    }

    public long getMasterSeed() {
        return mySave.getSaveMeta().getSeed();
    }

    public void loadGame() {
        this.player = new Player();
        this.readPlayer();
    }

    // Why does this not simply use saveGame()?
    public void unloadGame() {
        LOGGER.info("Unloading...");
        saveAndLeaveCurrentWorld();
        writePlayer();
    }

    public void saveGame() {
        LOGGER.info("Saving...");
        gamescreenCurrent.save();
        writePlayer();
    }

    // probably also TMP
    public void joinGame() {
        if (this.mySave.hasWorld(player.getLocationUuid())) {
            this.joinWorld(player.getLocationUuid());
        } else {
            // Check if this save has a spawn place, otherwise generate a new one
            // When generating a new world, place the player at spawn
            String id = createWorld("A nice World", pickGenerator(Registry.GENERATOR_REGISTRY.getGens()),
                    this.mySave.getSaveMeta().getSeed());// TODO Derive world seed from that master seed instead of
                                                         // using it directly
            joinWorld(id);
        }
//        FlatScreen flatscreen = new FlatScreen(scm.getGuiHelper());
//        this.gamescreenCurrent = flatscreen;
//        flatscreen.load();
//        scm.setGameScreen(flatscreen);
//        this.player.joinFlatWorld(flatscreen);
    }

    // TMP!!!!
    private IGeneratingLayer<WorldPrimer, GeneratorSettings> pickGenerator(
            Set<IGeneratingLayer<WorldPrimer, GeneratorSettings>> set) {
        List<IGeneratingLayer<WorldPrimer, GeneratorSettings>> list = new ArrayList<>();
        list.addAll(set);
        return MathUtil.getRandom(new Random(), list);
    }

    public void saveAndLeaveCurrentWorld() {
        // eh...
        if (gamescreenCurrent instanceof TileScreen) {
            this.player.leaveTileWorld((TileScreen) gamescreenCurrent);
        } else if (gamescreenCurrent instanceof FlatScreen) {
            this.player.leaveFlatWorld((FlatScreen) gamescreenCurrent);
        }
        this.gamescreenCurrent.unload();
        this.gamescreenCurrent = null;
    }

    public void joinWorld(String uuid) {
        try {
            if (!this.mySave.hasWorld(uuid)) {
                LOGGER.error("No such world with uuid: " + uuid);
                return;
            }
            LOGGER.infof("Setting up world for joining...");
            IWorldSave save = this.mySave.getWorld(uuid);
            TileScreen tilescreen = new TileScreen(scm.getGuiHelper(), save);
            this.gamescreenCurrent = tilescreen;

            tilescreen.load();

            LOGGER.info("Joining world...");
            scm.setGameScreen(tilescreen);
            this.player.joinTileWorld(tilescreen);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createWorld(String name, IGeneratingLayer<WorldPrimer, GeneratorSettings> generator, long seed) {
        LOGGER.infof("Creating world...");
        WorldPrimer worldPrimer = generator.generate(new GeneratorSettings(seed));
        WorldMeta wMeta = WorldMeta.builder().displayName(name).worldSeed(seed).createdNow()
                .worldGenerator(Registry.GENERATOR_REGISTRY.getId(generator)).dimensions(worldPrimer.getWorldBounds())
                .create();
        // The meta can probably be cached (useful for create-and-join)
        // See Save.java for todo on uuid in meta...?
        try {
            String uuid = this.mySave.createWorld(wMeta);
            return uuid;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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
