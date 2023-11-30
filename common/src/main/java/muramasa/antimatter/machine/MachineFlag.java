package muramasa.antimatter.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.machine.types.Machine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    EU, //Needs power
    RF, //Uses RF instead of EU
    HEAT,
    RECIPE, //Has a recipe map
    GUI,
    GENERATOR, //Has a recipe map and converts applicable recipes to power.
    COVERABLE,
    PARTIAL_AMPS;

    public static final MachineFlag[] VALUES;

    static {
        VALUES = values();
    }

    @Deprecated
    public void add(Machine<?> machine) {
        machine.addFlags(this);
    }

    @Deprecated
    public void remove(Machine<?> machine) {
        machine.removeFlags(this);
    }

    @Deprecated
    public Set<Machine<?>> getTypes() {
        return new HashSet<>(Machine.getTypes(this));
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
