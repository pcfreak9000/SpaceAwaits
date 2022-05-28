package mod;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;

public class ContainerShipSelect extends GuiOverlay {
    
    private Entity shipEntity;
    
    public ContainerShipSelect(Entity e) {
        this.shipEntity = e;
    }
    
    @Override
    protected void create() {
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table table = new Table();
        TextButton emInv = new TextButton("Emergency Pack", CoreRes.SKIN.getSkin());
        emInv.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                player.openContainer(
                        new ContainerInventoryShip(shipEntity.getComponent(ComponentInventoryShip.class).invShip));
            }
        });
        table.add(emInv).pad(5);
        table.row();
        TextButton disInv = new TextButton("Disassembler", CoreRes.SKIN.getSkin());
        disInv.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                player.openContainer(
                        new ContainerDisassembler(shipEntity.getComponent(DisassemblerComponent.class).disassembler,
                                shipEntity.getComponent(CompositeInventoryComponent.class).compositeInv));
            }
        });
        table.add(disInv).pad(5);
        table.row();
        TextButton crafterInv = new TextButton("Crafting Grid", CoreRes.SKIN.getSkin());
        crafterInv.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                player.openContainer(new ContainerCrafter(6));
            }
        });
        table.add(crafterInv).pad(5);
        table.row();
        ScrollPane pane = new ScrollPane(table, CoreRes.SKIN.getSkin());
        supertable.add(pane);
        stage.addActor(supertable);
    }
    
}
