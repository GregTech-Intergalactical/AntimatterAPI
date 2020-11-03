package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class BasicMachine extends Machine<BasicMachine> {

    public BasicMachine(String domain, String id, Object... data) {
        super(domain, id, data);
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        setTile(() -> new TileEntityMachine(this));
        setGUI(Data.BASIC_MENU_HANDLER);
    }
}