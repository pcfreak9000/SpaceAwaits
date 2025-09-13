package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.core.ContainerTesting;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.TileScreen;
import de.pcfreak9000.spaceawaits.flat.FlatScreen;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.knowledge.Experiences;
import de.pcfreak9000.spaceawaits.knowledge.Knowledgebase;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldSystem;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player implements INBTSerializable {

	public static enum GameMode {
		Survival(false), Testing(true), TestingGhost(true);

		public final boolean isTesting;

		private GameMode(boolean istesting) {
			this.isTesting = istesting;
		}
	}

	private TileWorldPlayer tileworldplayer;
	private FlatWorldPlayer flatworldplayer;

	private InventoryPlayer inventory;

	private Knowledgebase knowledges;
	private Experiences experiences;

	private GameMode gameMode = GameMode.Survival;
	private String currentLocation = "";
	// Hmmmmm...
	private Array<ItemStack> toDrop = new Array<>(false, 10);

	public Player() {
		this.inventory = new InventoryPlayer();
		this.knowledges = new Knowledgebase();
		this.experiences = new Experiences();
		this.tileworldplayer = new TileWorldPlayer(this);
		this.flatworldplayer = new FlatWorldPlayer(this);
	}

	public TileWorldPlayer getTileWorldPlayer() {
		return tileworldplayer;
	}

	public FlatWorldPlayer getFlatWorldPlayer() {
		return flatworldplayer;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode mode) {
		this.gameMode = mode;
	}

	public String getCurrentLevel() {
		return currentLocation;
	}

	public InventoryPlayer getInventory() {
		return this.inventory;
	}

	public Knowledgebase getKnowledge() {
		return this.knowledges;
	}

	public Experiences getExperience() {
		return this.experiences;
	}

	public void dropWhenPossible(ItemStack stack) {
		if (!ItemStack.isEmptyOrNull(stack)) {
			this.toDrop.add(stack);
		}
	}

	public Array<ItemStack> getToDrop() {
		return toDrop;
	}

	public float getReach() {
		return getGameMode().isTesting ? 200 : 10;// -> ReachComponent or something...
	}

	// have reach component??? maybe move this into hand component or so? and then
	// as parameter have an entity?
	public boolean isInReachFromHand(float x, float y, float range) {
		return true;
//        Vector2 pos = Components.TRANSFORM.get(getPlayerEntity()).position;// Hmm. Entity stuff here? oof
//        float xdif = x - pos.x;
//        float ydif = y - pos.y + 1;
//        return Mathf.square(xdif) + Mathf.square(ydif) < Mathf.square(range);
	}

	public void openContainer(GuiOverlay container) {
		container.createAndOpen(this);
	}

	public void openInventory() {
		if (gameMode.isTesting) {
			this.openContainer(new ContainerTesting());
		} else {
			this.openContainer(new ContainerInventoryPlayer());
		}
	}

	@Override
	public void readNBT(NBTCompound pc) {
		this.tileworldplayer.readNBT(pc.getCompound("twplayer"));
		this.flatworldplayer.readNBT(pc.getCompound("fwplayer"));
		this.inventory.readNBT(pc.getCompound("inventory"));
		this.knowledges.readNBT(pc.getCompound("science"));
		this.experiences.readNBT(pc.getCompound("experience"));
		NBTList todropl = pc.getListOrDefault("todrop", new NBTList(NBTType.Compound));
		for (int i = 0; i < todropl.size(); i++) {
			toDrop.add(ItemStack.readNBT(todropl.getCompound(i)));
		}
		this.gameMode = GameMode.values()[(int) pc.getIntegerSmartOrDefault("gamemode", GameMode.Survival.ordinal())];
		this.currentLocation = pc.getString("currentLocation");
	}

	@Override
	public void writeNBT(NBTCompound pc) {
		pc.putCompound("twplayer", INBTSerializable.writeNBT(tileworldplayer));
		pc.putCompound("fwplayer", INBTSerializable.writeNBT(flatworldplayer));
		pc.put("inventory", INBTSerializable.writeNBT(inventory));
		pc.put("science", INBTSerializable.writeNBT(knowledges));
		pc.put("experience", INBTSerializable.writeNBT(experiences));
		NBTList todropl = new NBTList(NBTType.Compound);
		for (ItemStack s : toDrop) {
			if (!ItemStack.isEmptyOrNull(s)) {
				todropl.add(ItemStack.writeNBT(s, new NBTCompound()));
			}
		}
		pc.put("todrop", todropl);
		pc.putIntegerSmart("gamemode", gameMode.ordinal());
		pc.putString("currentLocation", currentLocation);
	}

	public void join(GameScreen gamescreenCurrent, String uuid) {
		this.currentLocation = uuid;
		if (gamescreenCurrent instanceof TileScreen) {
			this.tileworldplayer.joinTileWorld((TileScreen) gamescreenCurrent);
		} else if (gamescreenCurrent instanceof FlatScreen) {
			this.flatworldplayer.joinFlatWorld((FlatScreen) gamescreenCurrent);
		}
	}

	public void leave(GameScreen gamescreenCurrent) {
		if (gamescreenCurrent instanceof TileScreen) {
			this.tileworldplayer.leaveTileWorld((TileScreen) gamescreenCurrent);
		} else if (gamescreenCurrent instanceof FlatScreen) {
			this.flatworldplayer.leaveFlatWorld((FlatScreen) gamescreenCurrent);
		}
		this.currentLocation = "";
	}

}
