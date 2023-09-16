package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.gui.widget.WidgetSupplier;

public class MultiMachine extends BasicMultiMachine<MultiMachine> {

    public MultiMachine(String domain, String name) {
        super(domain, name);
        setTile(BlockEntityMultiMachine::new);
        setGUI(Data.MULTI_MENU_HANDLER);
        covers((CoverFactory[]) null);
    }

    @Override
    protected void setupGui() {
        super.setupGui();
        addGuiCallback(t -> {
            BlockEntityMultiMachine<?> machine = (BlockEntityMultiMachine<?>) t.handler;
            WidgetSupplier wid = machine.getInfoWidget();
            if (wid != null) t.addWidget(wid);
        });
    }
}
