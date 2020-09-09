package muramasa.antimatter.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.machine.types.Machine;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.*;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public enum MachineFlag {

    BASIC(), //
    STEAM(),
    MULTI(), //Has structure
    HATCH(),
    ITEM(), //Can store items
    FLUID(),
    ENERGY(), //Needs power
    RECIPE(), //Has a recipe map
    GUI(),
    GENERATOR(), //Has a recipe map and converts applicable recipes to power.
    COVERABLE(),
    CONFIGURABLE();

    public static MachineFlag[] VALUES;

    static {
        VALUES = values();
    }

    private Set<Machine<?>> types = new ObjectOpenHashSet<>();

    public void add(Machine<?>... machines) {
        types.addAll(Arrays.asList(machines));
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
