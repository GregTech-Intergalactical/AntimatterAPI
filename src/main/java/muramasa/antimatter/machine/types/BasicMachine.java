package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class BasicMachine extends Machine<BasicMachine> {

    public BasicMachine(String domain, String id, Object... data) {
        super(domain, id, data);
        setTile(() -> new TileEntityMachine(this));
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
    }
}