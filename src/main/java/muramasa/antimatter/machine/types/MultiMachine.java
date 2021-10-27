package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;

public class MultiMachine extends BasicMultiMachine<MultiMachine> {

    public MultiMachine(String domain, String name) {
        super(domain, name);
        setTile(() -> new TileEntityMultiMachine<>(this));
        setGUI(Data.MULTI_MENU_HANDLER);
        covers((CoverFactory [])null);
    }

    @Override
    protected void setupGui() {
        super.setupGui();
        addGuiCallback(t -> {
            TileEntityMultiMachine<?> machine = (TileEntityMultiMachine<?>) t.handler;
            WidgetSupplier wid = machine.getInfoWidget();
            if (wid != null) t.addWidget(wid);
        });
    }
}
