package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.badlogic.gdx.utils.LongMap;

public class IDFactory {
	private static long id = 0;
	private static LongMap<Object> map = new LongMap<>();

	public static VoidPointer putData(Object obj) {
		id += 1;
		map.put(id, obj);
		return new VoidPointer(id, false);
	}

	public static Object obtainData(VoidPointer pointer) {
		if (pointer == null) {
			return null;
		}
		return map.get(pointer.getPointer());
	}

	public static void remove(VoidPointer pointer) {
		map.remove(pointer.getPointer());
		pointer.free();
	}
}
