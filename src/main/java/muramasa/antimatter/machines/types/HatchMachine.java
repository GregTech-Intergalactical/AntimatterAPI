package muramasa.antimatter.machines.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.tileentities.multi.TileEntityHatch;

import java.util.function.Supplier;

import static muramasa.antimatter.machines.MachineFlag.*;

public class HatchMachine extends Machine {

    public HatchMachine(String domain, String id, Supplier<? extends TileEntityHatch> tile, Object... data) {
        super(domain, id, tile, data);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, CONFIGURABLE, COVERABLE);
        if (hasFlag(GUI)) setGUI(Data.HATCH_MENU_HANDLER);
    }

    public HatchMachine(String domain, String id, Object... data) {
        this(domain, id, TileEntityHatch::new, data);
    }
}
