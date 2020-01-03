package muramasa.antimatter.machines.types;

import muramasa.gtu.data.Guis;
import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.tileentities.multi.TileEntityHatch;

import java.util.function.Supplier;

import static muramasa.antimatter.machines.MachineFlag.*;

public class HatchMachine extends Machine {

    public HatchMachine(String name, Supplier<? extends TileEntityHatch> tile, Object... data) {
        super(name, tile, data);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, CONFIGURABLE, COVERABLE);
        if (hasFlag(GUI)) setGUI(Guis.HATCH_MENU_HANDLER);
    }

    public HatchMachine(String name, Object... data) {
        this(name, TileEntityHatch::new, data);
    }
}
