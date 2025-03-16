package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.gui.GuiEsc;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.gui.Hud;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.render.DebugOverlay;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

public class GuiOverlaySystem extends EntitySystem implements RenderSystemMarker, Disposable {
    
    private GameScreen gamescreen;
    
    private GuiOverlay guiContainerCurrent;
    
    private boolean showDebugScreen;
    private DebugOverlay debugScreen;
    
    //Move the Hud to InventoryHandlerSystem or something?
    private Hud hud;
    
    private EventBus bus;
    
    public GuiOverlaySystem(GameScreen gs) {
        this.gamescreen = gs;
        this.debugScreen = new DebugOverlay(this.gamescreen);
        this.hud = new Hud(gs.getGuiHelper());
    }
    
    @EventSubscription
    private void ev(WorldEvents.PlayerJoinedEvent ev) {
        this.hud.setPlayer(ev.player);
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.bus = ((EngineImproved) engine).getEventBus();
    }
    
    //    @Override
    //    public void removedFromEngine(Engine engine) {
    //        super.removedFromEngine(engine);
    //        this.bus = null;
    //    }
    
    //Always takes a new GuiContainer. Is that the way to go?
    public void setGuiCurrent(GuiOverlay guicont) {
        if (guicont == null && isGuiContainerOpen()) {
            bus.post(new RendererEvents.CloseGuiOverlay(this.guiContainerCurrent));
            //Possibly closing logic first
            this.guiContainerCurrent.onClosed();
            this.guiContainerCurrent.dispose();
            InptMgr.multiplex(null);
            InptMgr.WORLD.setLocked(false);
            this.guiContainerCurrent = null;
        } else if (guicont != null) {
            if (isGuiContainerOpen()) {
                setGuiCurrent(null);
            }
            this.guiContainerCurrent = guicont;
            InptMgr.multiplex(guicont.getStage());
            InptMgr.WORLD.setLocked(true);
            this.guiContainerCurrent.onOpened();
            bus.post(new RendererEvents.OpenGuiOverlay(guicont));
            //Possibly opening logic
        }
    }
    
    public boolean isGuiContainerOpen() {
        return this.guiContainerCurrent != null;
    }
    
    @Override
    public void update(float delta) {
        if (InptMgr.UI.isJustPressed(EnumInputIds.DebugScreenButton)) {
            showDebugScreen = !showDebugScreen;
        }
        if (InptMgr.UI.isJustPressed(EnumInputIds.HideHud)) {
            this.gamescreen.setShowGuiElements(!this.gamescreen.isShowGuiElements());
        }
        if (!isGuiContainerOpen() && InptMgr.UI.isJustPressed(EnumInputIds.Esc)) {
            GuiEsc gesc = new GuiEsc();
            gesc.createAndOpen(null);//Hmmmmmmm
        }
        if (this.gamescreen.isShowGuiElements()) {
            this.hud.actAndDraw(delta);
            if (showDebugScreen) {
                this.debugScreen.actAndDraw(delta);
            }
            if (this.guiContainerCurrent != null) {
                this.guiContainerCurrent.actAndDraw(delta);
            }
        }
    }
    
    @Override
    public void dispose() {
        setGuiCurrent(null);
    }
}
