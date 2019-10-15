package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;

import java.util.function.Supplier;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class HatchMachine extends Machine {

    public HatchMachine(String name, Supplier<? extends TileEntityHatch> tile, Object... data) {
        super(name, tile, data);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, CONFIGURABLE, COVERABLE);
        if (hasFlag(GUI)) setGUI(GregTech.INSTANCE, Ref.GUI_ID_HATCH);
    }

    public HatchMachine(String name, Object... data) {
        this(name, TileEntityHatch::new, data);
    }
}
