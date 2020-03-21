package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityTank;

import static muramasa.antimatter.machine.MachineFlag.*;

public class TankMachine extends Machine {

    public TankMachine(String domain, String name, Object... data) {
        super(domain, name, TileEntityTank::new, data);
        addFlags(BASIC, ITEM, FLUID, COVERABLE, CONFIGURABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
    }
}
