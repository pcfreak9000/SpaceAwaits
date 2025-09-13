package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.core.screen.TileScreen;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;

public class LevelTypeTiles extends LevelType {
	
	public static final LevelTypeTiles LTT = new LevelTypeTiles();
	
	@Override
	public GameScreen createGameScreen(GuiHelper gh, IWorldSave worldsave) {
		return new TileScreen(gh, worldsave);
	}

	@Override
	public void initializeLevel(LevelCreationVisitor visitor, ILevelSave save) {
		TilesLevelCreationVisitor tvis = (TilesLevelCreationVisitor) visitor;
		long seed = tvis.visitSeed();
		IGeneratingLayer<WorldPrimer, GeneratorSettings> generator = tvis.visitGenerator();
		String name = tvis.visitDisplayName();
		WorldPrimer worldPrimer = generator.generate(new GeneratorSettings(seed));
		WorldMeta wMeta = WorldMeta.builder().displayName(name).worldSeed(seed).createdNow()
				.worldGenerator(Registry.GENERATOR_REGISTRY.getId(generator)).dimensions(worldPrimer.getWorldBounds())
				.create();
		save.writeMeta(wMeta);
	}

	@Override
	public String getTypeID() {
		return "tiles";
	}

}
