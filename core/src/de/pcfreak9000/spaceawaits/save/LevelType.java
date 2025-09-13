package de.pcfreak9000.spaceawaits.save;

import com.badlogic.gdx.utils.ObjectMap;

import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;

public abstract class LevelType {
	
	private static ObjectMap<String, LevelType> types = new ObjectMap<>();
	
	public static LevelType getLevelType(String typeid) {
		return types.get(typeid);
	}
	
	public LevelType() {
		if(types.containsKey(getTypeID())) {
			throw new IllegalStateException();
		}
		types.put(getTypeID(), this);
	}
	
	public abstract GameScreen createGameScreen(GuiHelper gh, IWorldSave worldsave);
	
	public abstract void initializeLevel(LevelCreationVisitor visitor, ILevelSave save);
	
	public abstract String getTypeID();
	
}
