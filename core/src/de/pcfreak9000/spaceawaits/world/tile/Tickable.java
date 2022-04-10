package de.pcfreak9000.spaceawaits.world.tile;

//TODO maybe then just have an Entity for the ECS instead of manually doing this here?
public interface Tickable {
    void tick(float dtime, long tickIndex);
}
