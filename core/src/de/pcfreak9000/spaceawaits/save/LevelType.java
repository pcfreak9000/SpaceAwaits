package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;

public interface LevelType {

	GameScreen createGameScreen(GuiHelper gh, IWorldSave worldsave);
	
	
	
}
