package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class HatchMachine extends Machine {

    public HatchMachine(String name, Class<? extends TileEntityMachine> tileClass, Object... data) {
        super(name, tileClass, data);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, CONFIGURABLE, COVERABLE);
        if (hasFlag(GUI)) setGUI(GregTech.INSTANCE, Ref.GUI_ID_HATCH);
    }

    public HatchMachine(String name, Object... data) {
        this(name, TileEntityHatch.class, data);
    }
}
