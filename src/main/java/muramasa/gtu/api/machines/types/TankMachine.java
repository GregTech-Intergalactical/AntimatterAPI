package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.tileentities.TileEntityTank;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class TankMachine extends Machine {

    public TankMachine(String name, Object... data) {
        super(name, TileEntityTank::new, data);
        addFlags(BASIC, ITEM, FLUID, COVERABLE, CONFIGURABLE);
        setGUI(Guis.BASIC_MENU_HANDLER);
    }
}
