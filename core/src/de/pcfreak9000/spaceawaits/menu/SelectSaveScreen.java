package de.pcfreak9000.spaceawaits.menu;

import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;

import de.pcfreak9000.spaceawaits.core.CoreResources;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.save.SaveMeta;

public class SelectSaveScreen extends ScreenAdapter {
    
    private static final class SaveMetaUi {
        private final SaveMeta meta;
        
        public SaveMetaUi(SaveMeta m) {
            this.meta = m;
        }
        
        @Override
        public String toString() {
            return meta.getDisplayName();
        }
    }
    
    private ExtendViewport viewport;
    private FillViewport backgroundVp;
    
    private SpriteBatch batch;//TODO global batch? also see ChunkRenderer with SpriteCache
    
    private Stage stage;
    private Skin skin;
    private Table table = new Table();
    private List<SaveMetaUi> savesList;
    
    public SelectSaveScreen() {
        this.skin = new Skin(Gdx.files.internal("ui/skin.json"));//TODO license stuff, resource loading stuff
        viewport = new ExtendViewport(200, 200);
        this.stage = new Stage(viewport);
        savesList = new List<>(skin);
        backgroundVp = new FillViewport(200 * 16 / 9f, 200);
        TextButton newButton = new TextButton("New", skin);
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SpaceAwaits.getSpaceAwaits().getGameManager().createAndLoadGame(UUID.randomUUID().toString(),
                        new Random().nextLong());//TODO Use some other random instead
                SpaceAwaits.getSpaceAwaits().setScreen(SpaceAwaits.getSpaceAwaits().worldRenderer);
            }
        });
        
        TextButton playSelectedButton = new TextButton("Play selected", skin);
        playSelectedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMetaUi selected = savesList.getSelected();
                SpaceAwaits.getSpaceAwaits().getGameManager().loadGame(selected.meta.getNameOnDisk());
                SpaceAwaits.getSpaceAwaits().setScreen(SpaceAwaits.getSpaceAwaits().worldRenderer);
            }
        });
        
        ClickListener listListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMetaUi selected = savesList.getSelected();
                SpaceAwaits.getSpaceAwaits().getGameManager().loadGame(selected.meta.getNameOnDisk());
                SpaceAwaits.getSpaceAwaits().setScreen(SpaceAwaits.getSpaceAwaits().worldRenderer);
            };
        };
        savesList.addListener(listListener);
        ScrollPane pane = new ScrollPane(savesList);
        this.batch = new SpriteBatch();
        table.setWidth(this.stage.getWidth());
        table.setHeight(this.stage.getHeight());
        table.align(Align.left);
        
        Table buttontable = new Table();
        buttontable.add(newButton).width(100).padBottom(5);
        buttontable.row();
        buttontable.add(playSelectedButton).width(100);
        table.add(buttontable);
        table.add(pane);
        stage.addActor(table);
    }
    
    private void updateSavesListEntries() {
        Array<SaveMetaUi> array = new Array<>(SpaceAwaits.getSpaceAwaits().getGameManager().listSaves().stream()
                .map((sm) -> new SaveMetaUi(sm)).toArray(SaveMetaUi[]::new));
        savesList.setItems(array);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        backgroundVp.update(width, height);
    }
    
    @Override
    public void show() {
        updateSavesListEntries();
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        backgroundVp.apply(true);
        batch.setProjectionMatrix(backgroundVp.getCamera().combined);
        batch.begin();
        float w = backgroundVp.getWorldWidth();
        float h = backgroundVp.getWorldHeight();
        batch.draw(CoreResources.SPACE_BACKGROUND.getRegion(), 0, 0, w, h);
        batch.end();
        viewport.apply();
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();
        this.batch.dispose();
    }
}
