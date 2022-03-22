package muramasa.antimatter.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.machine.types.Machine;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public enum MachineFlag {

    BASIC, //
    STEAM,
    MULTI, //Has structure
    HATCH,
    FAKE_INPUTS,
    ITEM, //Can store items
    ITEM_INPUT,
    ITEM_OUTPUT,
    CELL,
    CELL_INPUT,
    CELL_OUTPUT,
    FLUID_INPUT,
    FLUID_OUTPUT,
    FLUID,
    ENERGY, //Needs power
    HEAT,
    RECIPE, //Has a recipe map
    GUI,
    GENERATOR, //Has a recipe map and converts applicable recipes to power.
    COVERABLE;

    public static final MachineFlag[] VALUES;

    static {
        VALUES = values();
    }

    private final Set<Machine<?>> types = new ObjectOpenHashSet<>();

    public void add(Machine<?> machine) {
        this.types.add(machine);
    }

    public void remove(Machine<?> machine) {
        this.types.remove(machine);
    }

    public Set<Machine<?>> getTypes() {
        return types;
    }

    public static Collection<Machine<?>> getTypes(MachineFlag... flags) {
        List<Machine<?>> types = new ObjectArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }

}
