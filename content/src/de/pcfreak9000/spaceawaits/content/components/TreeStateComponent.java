package de.pcfreak9000.spaceawaits.content.components;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsTreeState")
public class TreeStateComponent implements Component {
    @NBTSerialize(key = "l")
    public boolean loose;
    
}
