package de.pcfreak9000.spaceawaits.core.screen;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.DynamicAssetListener;
import de.pcfreak9000.spaceawaits.core.assets.WatchDynamicAssetAnnotationProcessor;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.ecs.Saveable;
import de.pcfreak9000.spaceawaits.core.ecs.Transferable;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

public class GameScreen extends ScreenAdapter {
	public static final float STEPLENGTH_SECONDS = 1 / 60f;

	/* Technical stuff */

	private GuiHelper guiHelper;
	private RenderHelper2D renderHelper2D;

	private float renderTime = 0;

	// for debugging and making sure an unloaded screen is not loaded again
	private int canuse = 0;// 0->initialized, 1->loaded, 2->unloaded

	/* Game related utilities */

	private boolean saveAndExitToMainMenu = false;
	private boolean showGuiElements = true;

	protected final EngineImproved ecsEngine;

	// Hmmm...
	private ICommandContext commands;

	public GameScreen(GuiHelper guiHelper, ICommandContext commands) {
		this.guiHelper = guiHelper;
		this.commands = commands;
		this.renderHelper2D = new RenderHelper2D();

		this.ecsEngine = new EngineImproved(STEPLENGTH_SECONDS);
	}

	public GuiHelper getGuiHelper() {
		return guiHelper;
	}

	public RenderHelper2D getRenderHelper() {
		return renderHelper2D;
	}

	public boolean isShowGuiElements() {
		return showGuiElements;
	}

	public void setShowGuiElements(boolean b) {
		this.showGuiElements = b;
	}

	@Override
	public void show() {
		InptMgr.init();
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
		this.dispose();
	}

	@Override
	public void render(float delta) {
		renderTime += delta;

		ScreenUtils.clear(0, 0, 0, 1);
		SpaceAwaits.BUS.post(new RendererEvents.PreFrameEvent());
		ecsEngine.update(delta);
		if (saveAndExitToMainMenu) {
			SpaceAwaits.getSpaceAwaits().getGameManager().unloadGame();
		}
	}

	public void queueSaveAndExitToMainMenu() {
		saveAndExitToMainMenu = true;
	}

	@Override
	public void resize(int width, int height) {
		this.guiHelper.resize(width, height);
		SpaceAwaits.BUS.post(new RendererEvents.ResizeWorldRendererEvent(this, width, height));
	}

	@Override
	public void dispose() {
		super.dispose();
		this.renderHelper2D.dispose();
	}

	public float getRenderTime() {
		return renderTime;
	}

	public ICommandContext getCommandContext() {
		return commands;
	}

	public <T extends EntitySystem> T getSystem(Class<T> clazz) {
		return ecsEngine.getSystem(clazz);
	}

	// Hmm??
	public EngineImproved getEngine() {
		return ecsEngine;
	}

	public EventBus getWorldBus() {
		return this.ecsEngine.getEventBus();
	}

	public void save() {
		EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
		for (EntitySystem es : syss) {
			if (es instanceof Saveable) {
				Saveable u = (Saveable) es;
				u.save();
			}
		}
	}

	public void load() {
		if (canuse > 0) {
			throw new IllegalStateException();
		}
		// FIXME this is called after addition of Systems which might alreeady add
		// entities with DynamicAssets...
		for (DynamicAssetListener<Component> dal : WatchDynamicAssetAnnotationProcessor.get()) {
			ecsEngine.addEntityListener(dal.getFamily(), dal);
		}
		EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
		for (EntitySystem es : syss) {
			if (es instanceof Transferable) {
				Transferable u = (Transferable) es;
				u.load();
			}
		}
		SpaceAwaits.BUS.register(this.ecsEngine.getEventBus());// Not too sure about this
		canuse = 1;
	}

	public void unload() {
		if (canuse != 1) {
			throw new IllegalStateException();
		}
		// can't use ecsEngine.removeAllSystems(); because systems need to be
		// unregistered
		EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
		for (EntitySystem es : syss) {
			if (es instanceof Transferable) {
				Transferable u = (Transferable) es;
				u.unload();
			}
		}

		ecsEngine.removeAllEntities();

		// first decouple...
		for (EntitySystem es : syss) {
			ecsEngine.removeSystem(es);
			SpaceAwaits.BUS.unregister(es);// Forcefully unregister systems which would otherwise be dangling. Systems
											// shouldn't register to this BUS anyways, at least usually.
		}
		// ...then dispose
		for (EntitySystem es : syss) {
			if (es instanceof Disposable) {
				Disposable d = (Disposable) es;
				d.dispose();
			}
		}

		// unregister all other listener of the worldbus?

		SpaceAwaits.BUS.unregister(ecsEngine.getEventBus());

		for (DynamicAssetListener<Component> dal : WatchDynamicAssetAnnotationProcessor.get()) {
			ecsEngine.removeEntityListener(dal);
		}
		canuse = 2;
	}

}
