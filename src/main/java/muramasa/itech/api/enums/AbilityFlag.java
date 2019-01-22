package muramasa.itech.api.enums;

import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.machines.Tier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public enum AbilityFlag {

    BASIC(), //
    MULTI(), //Has structure
    HATCH(),
    ITEM(), //Can store items
    FLUID(), //Can store fluids
    POWERED(), //Needs power
    RECIPE(); //Has a recipe map

    private int bit;
    private ArrayList<Machine> typeList;

    AbilityFlag() {
        bit = 1 << ordinal();
        typeList = new ArrayList<>();
    }

    public void add(Machine... machines) {
        typeList.addAll(Arrays.asList(machines));
    }

    public int getBit() {
        return bit;
    }

    public Collection<Machine> getTypes() {
        return typeList;
    }

    public Collection<MachineStack> getStacks() {
        ArrayList<MachineStack> stacks = new ArrayList<>();
        for (Machine machine : typeList) {
            for (Tier tier : machine.getTiers()) {
                stacks.add(new MachineStack(machine, tier));
            }
        }
        return stacks;
    }
}
