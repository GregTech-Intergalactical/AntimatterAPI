package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Slot;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.common.blocks.BlockHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.utils.Ref;

import static muramasa.gregtech.api.machines.MachineFlag.HATCH;

public class HatchMachine extends Machine {

    public HatchMachine(String name, MachineFlag flag, Machine machine, Slot... slots) {
        this(name, flag, machine.getSlots().toArray(new Slot[0]));
        addSlots(slots);
    }

    public HatchMachine(String name, MachineFlag flag, Slot... slots) {
        this(name, flag);
        addSlots(slots);
        addGUI(GregTech.INSTANCE, Ref.HATCH_ID);
    }

    public HatchMachine(String name, MachineFlag flag) {
        super(name, new BlockHatch(name), TileEntityHatch.class);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, flag);
    }
}
