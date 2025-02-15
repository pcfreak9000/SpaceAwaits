package de.pcfreak9000.spaceawaits.core.ecs.content;

public interface Tickable {
    void tick(float dtime, long tickIndex);
}
