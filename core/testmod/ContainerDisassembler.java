
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.comp.CompositeInventory;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ContainerDisassembler extends GuiInventory {
    private DisassemblerInventory dinv;
    private Disassembler disassembler;
    private CompositeInventory compInv;
    
    public ContainerDisassembler(Disassembler dis, CompositeInventory compInv) {
        this.disassembler = dis;
        this.compInv = compInv;
        this.dinv = new DisassemblerInventory(dis);
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        subtable.add(registerSlot(new Slot(dinv, 0))).pad(1f);
        TextButton b = new TextButton("Disassemble", CoreRes.SKIN.getSkin());
        b.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (disassembler.canDisassemble(dinv.getStack(0))) {
                    disassembler.disassemble(dinv.removeStack(0), compInv);
                }
            }
        });
        subtable.add(b).pad(1f);
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
    
    @Override
    public void onClosed() {
        super.onClosed();
        ItemStack s = dinv.removeStack(0);
        if (!ItemStack.isEmptyOrNull(s)) {
            s = InvUtil.insert(this.player.getInventory(), s);
        }
        if (!ItemStack.isEmptyOrNull(s)) {
            //TODO drop item?
        }
    }
}
