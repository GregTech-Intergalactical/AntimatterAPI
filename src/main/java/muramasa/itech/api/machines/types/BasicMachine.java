package muramasa.itech.api.machines.types;

import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.objects.MachineStack;
import muramasa.itech.api.machines.objects.SlotData;
import muramasa.itech.api.machines.objects.Tier;
import muramasa.itech.loaders.ContentLoader;

public class BasicMachine extends Machine {

    private SlotData[] slotData;
    private int inputCount, outputCount;

    public BasicMachine(String name, Tier[] tiers, SlotData... slots) {
        this(name, true, tiers, slots);
    }

    public BasicMachine(String name, boolean hasRecipes, Tier[] tiers, SlotData... slots) {
        super(name, hasRecipes);
        this.slotData = slots;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].type == 0) {
                inputCount++;
            } else if (slots[i].type == 1) {
                outputCount++;
            }
        }
        for (int i = 0; i < tiers.length; i++) {
            if (getName().contains("hatch")) {
                MachineList.hatchStackLookup.put(getName() + tiers[i].getName(), new MachineStack(this, tiers[i], ContentLoader.blockHatches));
            } else {
                MachineList.basicStackLookup.put(getName() + tiers[i].getName(), new MachineStack(this, tiers[i], ContentLoader.blockMachines));
            }
        }
        if (getName().contains("hatch")) {

        } else {
            MachineList.basicTypeLookup.put(getName(), this);
        }
    }

    public BasicMachine(String name, Tier[] tiers, Machine machine) {
        this(name, tiers, ((BasicMachine)machine).getSlotData());
    }

    public SlotData[] getSlotData() {
        return slotData;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }
}
