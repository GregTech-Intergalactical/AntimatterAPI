package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class HatchMachine extends Machine {

    public HatchMachine(String name, MachineFlag... flags) {
        super(name, new BlockMachine(name), TileEntityHatch.class);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, CONFIGURABLE, COVERABLE);
        addFlags(flags);
        if (hasFlag(GUI)) {
            addGUI(GregTech.INSTANCE, Ref.HATCH_ID);
        }
    }

    public HatchMachine(String name, Class tileClass, MachineFlag... flags) {
        this(name, flags);
        setTileClass(tileClass);
    }
}
