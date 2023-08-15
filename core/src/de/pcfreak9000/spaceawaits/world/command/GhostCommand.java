package de.pcfreak9000.spaceawaits.world.command;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "ghost")
public class GhostCommand implements Runnable {
    @Parameters(index = "0")
    public boolean ghost;
    
    @Override
    public void run() {
        Entity e = SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer().getPlayerEntity();
        for(Fixture f : Components.PHYSICS.get(e).body.getBody().getFixtureList()) {
            f.setSensor(ghost);
        }
    }
    
}
