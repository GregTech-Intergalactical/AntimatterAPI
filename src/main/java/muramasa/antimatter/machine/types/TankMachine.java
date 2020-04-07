package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityTank;

import static muramasa.antimatter.machine.MachineFlag.*;

public class TankMachine extends Machine<TankMachine> {

    public TankMachine(String domain, String name, Object... data) {
        super(domain, name, data);
        setTile(() -> new TileEntityTank(this));
        addFlags(BASIC, ITEM, FLUID, COVERABLE, CONFIGURABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
    }
}
