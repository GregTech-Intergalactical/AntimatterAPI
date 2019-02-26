package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.common.blocks.BlockHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.utils.Ref;

import static muramasa.gregtech.api.machines.MachineFlag.GUI;
import static muramasa.gregtech.api.machines.MachineFlag.HATCH;

public class HatchMachine extends Machine {

    public HatchMachine(String name, MachineFlag... flags) {
        super(name, new BlockHatch(name), TileEntityHatch.class);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH);
        addFlags(flags);
        if (hasFlag(GUI)) {
            addGUI(GregTech.INSTANCE, Ref.HATCH_ID);
        }
    }
}
