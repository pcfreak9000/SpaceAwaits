package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.gui.GuiEsc;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.gui.Hud;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.render.DebugOverlay;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

public class GuiOverlaySystem extends EntitySystem implements RenderSystemMarker {
    
    private GameScreen gamescreen;
    
    private GuiOverlay guiContainerCurrent;
    
    private boolean showDebugScreen;
    private DebugOverlay debugScreen;
    
    private Hud hud;
    
    public GuiOverlaySystem(GameScreen gs) {
        this.gamescreen = gs;
        this.debugScreen = new DebugOverlay(this.gamescreen);
        this.hud = new Hud(gs.getGuiHelper());
    }
    
    //Always takes a new GuiContainer. Is that the way to go?
    public void setGuiCurrent(GuiOverlay guicont) {
        if (guicont == null && isGuiContainerOpen()) {
            SpaceAwaits.BUS.post(new RendererEvents.CloseGuiOverlay(this.guiContainerCurrent));
            //Possibly closing logic first
            this.guiContainerCurrent.onClosed();
            this.guiContainerCurrent.dispose();
            InptMgr.multiplex(null);
            this.guiContainerCurrent = null;
        } else if (guicont != null) {// if (!isGuiContainerOpen())
            if (isGuiContainerOpen()) {
                setGuiCurrent(null);
            }
            this.guiContainerCurrent = guicont;
            InptMgr.multiplex(guicont.getStage());
            this.guiContainerCurrent.onOpened();
            SpaceAwaits.BUS.post(new RendererEvents.OpenGuiOverlay(guicont));
            //Possibly opening logic
        }
    }
    
    public boolean isGuiContainerOpen() {
        return this.guiContainerCurrent != null;
    }
    
    @Override
    public void update(float delta) {
        if (InptMgr.isJustPressed(EnumInputIds.DebugScreenButton)) {
            showDebugScreen = !showDebugScreen;
        }
        if (InptMgr.isJustPressed(EnumInputIds.HideHud)) {
            this.gamescreen.setShowGui(!this.gamescreen.isShowGui());
        }
        if (this.guiContainerCurrent == null && InptMgr.isJustPressed(EnumInputIds.Esc)) {
            GuiEsc gesc = new GuiEsc();
            gesc.createAndOpen(null);//Hmmmmmmm
        }
        if (this.gamescreen.isShowGui()) {
            this.hud.actAndDraw(delta);
            if (showDebugScreen) {
                this.debugScreen.actAndDraw(delta);
            }
            if (this.guiContainerCurrent != null) {
                this.guiContainerCurrent.actAndDraw(delta);
            }
        }
    }
    
    @Deprecated
    public void setPlayer(Player player) {
        this.hud.setPlayer(player);
    }
}
