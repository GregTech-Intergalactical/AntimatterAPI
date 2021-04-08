package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;

public class MultiMachine extends BasicMultiMachine<MultiMachine> {

    public MultiMachine(String domain, String name, Object... data) {
        super(domain, name, data);
        setTile(() -> new TileEntityMultiMachine(this));
        setGUI(Data.MULTI_MENU_HANDLER);
    }
}
