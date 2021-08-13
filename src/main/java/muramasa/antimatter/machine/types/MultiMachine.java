package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;

public class MultiMachine extends BasicMultiMachine<MultiMachine> {

    public MultiMachine(String domain, String name) {
        super(domain, name);
        setTile(() -> new TileEntityMultiMachine(this));
        setGUI(Data.MULTI_MENU_HANDLER);
        covers((ICover[])null);
        addGuiCallback(t -> t.addWidget(InfoRenderWidget.build()));
    }
}
