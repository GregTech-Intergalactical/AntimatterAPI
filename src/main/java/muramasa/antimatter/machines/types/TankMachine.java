package muramasa.antimatter.machines.types;

import muramasa.gtu.data.Guis;
import muramasa.antimatter.tileentities.TileEntityTank;

import static muramasa.antimatter.machines.MachineFlag.*;

public class TankMachine extends Machine {

    public TankMachine(String name, Object... data) {
        super(name, TileEntityTank::new, data);
        addFlags(BASIC, ITEM, FLUID, COVERABLE, CONFIGURABLE);
        setGUI(Guis.BASIC_MENU_HANDLER);
    }
}
