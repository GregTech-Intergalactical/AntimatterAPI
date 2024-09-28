package muramasa.antimatter.capability.machine;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.fluid.FluidTank;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MultiMachineFluidHandler<T extends BlockEntityMultiMachine<T>> extends MachineFluidHandler<T> {

    MachineFluidHandler<?>[] inputs = new MachineFluidHandler[0];
    MachineFluidHandler<?>[] outputs = new MachineFluidHandler[0];

    public MultiMachineFluidHandler(T tile) {
        super(tile);
        tanks.clear();
    }

    protected void cacheInputs() {
        inputs = tile.getComponentsByHandlerId(inputComponentString()).stream().map(IComponentHandler::getFluidHandler).map(Optional::get).sorted(this::compareInputHatches).toArray(MachineFluidHandler<?>[]::new);//this::allocateExtraSize);
        tanks.put(FluidDirection.INPUT, new FluidTanks(Arrays.stream(inputs).filter(t -> t.getInputTanks() != null).flatMap(t -> Arrays.stream(t.getInputTanks().getBackingTanks())).collect(Collectors.toList())));
    }

    protected int compareInputHatches(MachineFluidHandler<?> a, MachineFluidHandler<?> b) {
        return 0;
    }

    protected String inputComponentString(){
        return "fluid_input";
    }

    protected String outputComponentString(){
        return "fluid_output";
    }

    protected void cacheOutputs() {
        outputs = tile.getComponentsByHandlerId(outputComponentString()).stream().map(IComponentHandler::getFluidHandler).map(Optional::get).sorted(this::compareOutputHatches).toArray(MachineFluidHandler<?>[]::new);//this::allocateExtraSize);
        tanks.put(FluidDirection.OUTPUT, new FluidTanks(Arrays.stream(outputs).filter(t -> t.getOutputTanks() != null).flatMap(t -> Arrays.stream(t.getOutputTanks().getBackingTanks())).collect(Collectors.toList())));
    }

    protected int compareOutputHatches(MachineFluidHandler<?> a, MachineFluidHandler<?> b) {
        return 0;
    }

    public void invalidate() {
        tanks.clear();
        inputs = new MachineFluidHandler[0];
        outputs = new MachineFluidHandler[0];
    }

    public void onStructureBuild() {
        cacheInputs();
        cacheOutputs();
    }
}
