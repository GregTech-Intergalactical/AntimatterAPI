package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.blocks.BlockMachine;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class HatchMachine extends Machine {

    public HatchMachine(String name, MachineFlag... flags) {
        super(name, new BlockMachine(name), TileEntityHatch.class);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, CONFIGURABLE, COVERABLE);
        addFlags(flags);
        if (hasFlag(GUI)) addGUI(GregTech.INSTANCE, Ref.GUI_ID_HATCH);
    }

    public HatchMachine(String name, Class tileClass, MachineFlag... flags) {
        this(name, flags);
        this.tileClass = tileClass;
    }
}
