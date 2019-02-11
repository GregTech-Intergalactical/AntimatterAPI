package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.common.utils.Ref;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String name, MachineFlag... extraFlags) {
        super(name, new BlockMachine(name), TileEntityBasicMachine.class);
        setTiers(Tier.getStandard());
        addFlags(BASIC, ITEM, ENERGY, COVERABLE, CONFIGURABLE);
        addFlags(extraFlags);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MACHINE_ID);
    }
}
