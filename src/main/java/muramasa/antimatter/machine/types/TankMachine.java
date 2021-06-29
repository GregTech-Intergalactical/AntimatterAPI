package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityTank;

import static muramasa.antimatter.machine.MachineFlag.*;

public class TankMachine extends Machine<TankMachine> {

    public TankMachine(String domain, String name) {
        super(domain, name);
        setTile(() -> new TileEntityTank(this));
        addFlags(ITEM, FLUID, COVERABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
    }
}
