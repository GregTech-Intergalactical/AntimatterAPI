package muramasa.gregtech.api.machines;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.enums.MachineFlag;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.loaders.ContentLoader;

import static muramasa.gregtech.api.enums.MachineFlag.HATCH;

public class HatchMachine extends Machine {

    public HatchMachine(String name, MachineFlag... extraFlags) {
        super(name, ContentLoader.blockMachines, TileEntityHatch.class);
        setTiers(Tier.getStandard());
        addFlags(HATCH);
        addFlags(extraFlags);
        addGUI(GregTech.INSTANCE, Ref.HATCH_ID);
    }
}
