package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.multi.TileEntityHatch;

import static muramasa.antimatter.machine.MachineFlag.*;

public class HatchMachine extends Machine<HatchMachine> {

    public HatchMachine(String domain, String id, Object... data) {
        super(domain, id, data);
        setTile(() -> new TileEntityHatch(this));
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, CONFIGURABLE, COVERABLE);
        if (has(GUI)) setGUI(Data.HATCH_MENU_HANDLER);
    }
}
