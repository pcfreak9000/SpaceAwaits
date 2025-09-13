package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;

public class TilesLevelCreationVisitor implements LevelCreationVisitor {

	private long seed;
	private IGeneratingLayer<WorldPrimer, GeneratorSettings> generator;
	private String displayname;

	public TilesLevelCreationVisitor(long seed, IGeneratingLayer<WorldPrimer, GeneratorSettings> generator,
			String displayname) {
		this.seed = seed;
		this.generator = generator;
		this.displayname = displayname;
	}

	@Override
	public long visitSeed() {
		return seed;
	}

	public IGeneratingLayer<WorldPrimer, GeneratorSettings> visitGenerator() {
		return generator;
	}

	public String visitDisplayName() {
		return displayname;
	}

}
