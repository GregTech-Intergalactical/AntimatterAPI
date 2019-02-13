package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.machines.Slot;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.common.utils.Ref;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String name, Machine machine, Slot... slots) {
        this(name, machine.getSlots().toArray(new Slot[0]));
        addSlots(slots);
    }

    public BasicMachine(String name, Slot... slots) {
        super(name, new BlockMachine(name), TileEntityBasicMachine.class);
        setTiers(Tier.getStandard());
        setFlags(BASIC, ITEM, ENERGY, COVERABLE, CONFIGURABLE);
        addSlots(slots);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MACHINE_ID);
    }
}
