package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import speiger.src.collections.ints.maps.impl.hash.Int2ObjectOpenHashMap;
import speiger.src.collections.ints.maps.interfaces.Int2ObjectMap;
import speiger.src.collections.objects.maps.impl.hash.Object2IntOpenHashMap;
import speiger.src.collections.objects.maps.interfaces.Object2IntMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.stream.Collectors;

public class MultiMachineFluidHandler<T extends TileEntityMultiMachine<T>> extends MachineFluidHandler<T> {

    MachineFluidHandler<?>[] inputs = new MachineFluidHandler[0];
    MachineFluidHandler<?>[] outputs = new MachineFluidHandler[0];

    protected Int2ObjectMap<MachineFluidHandler<?>> INPUT_TO_HANDLER = new Int2ObjectOpenHashMap<>();
    protected Object2IntMap<MachineFluidHandler<?>> INPUT_START = new Object2IntOpenHashMap<>();
    protected int INPUT_END;
    protected Int2ObjectMap<MachineFluidHandler<?>> OUTPUT_TO_HANDLER = new Int2ObjectOpenHashMap<>();
    protected Object2IntMap<MachineFluidHandler<?>> OUTPUT_START = new Object2IntOpenHashMap<>();

    protected final EnumMap<FluidDirection, FluidTanks> tanks = new EnumMap<>(FluidDirection.class);

    public MultiMachineFluidHandler(T tile) {
        super(tile);
    }

    @Override
    public boolean canOutputsFit(FluidStack[] outputs) {
        return outputs != null && this.outputs != null && this.outputs.length >= outputs.length;
    }

    protected void cacheInputs() {
        inputs = tile.getComponents("hatch_fluid_input").stream().map(IComponentHandler::getFluidHandler).filter(LazyOptional::isPresent).map(t -> (t.resolve().get())).toArray(MachineFluidHandler<?>[]::new);//this::allocateExtraSize);
        // handlers[handlers.length-1] = this.inputWrapper;
        INPUT_TO_HANDLER.clear();
        INPUT_START.clear();
        int i = 0;
        for (MachineFluidHandler<?> input : inputs) {
            for (int j = 0; j < input.getTanks(); j++) {
                INPUT_TO_HANDLER.put(j+i, input);
                if (j == 0) INPUT_START.put(input,i);
            }
            i += input.getTanks();
        }
        INPUT_END = i;
    }

    protected void cacheOutputs() {
        outputs = tile.getComponents("hatch_fluid_output").stream().map(IComponentHandler::getFluidHandler).filter(LazyOptional::isPresent).map(t -> (t.resolve().get())).toArray(MachineFluidHandler<?>[]::new);//this::allocateExtraSize);
        // handlers[handlers.length-1] = this.inputWrapper;
        OUTPUT_TO_HANDLER.clear();
        OUTPUT_START.clear();
        int i = 0;
        for (MachineFluidHandler<?> output : outputs) {
            for (int j = 0; j < output.getTanks(); j++) {
                OUTPUT_TO_HANDLER.put(j+i, output);
                if (j == 0) OUTPUT_START.put(output,i);
            }
            i += output.getTanks();
        }
    }

    //TODO: Remove gettanks() != null as this is called twice.
    @Nullable
    @Override
    public FluidTanks getInputTanks() {
        //Input tanks output into the machine.
        return new FluidTanks(Arrays.stream(inputs).filter(t -> t.getInputTanks() != null).flatMap(t -> Arrays.stream(t.getInputTanks().getBackingTanks())).collect(Collectors.toList()));
    }
    //TODO: Remove gettanks() != null as this is called twice.
    @Nullable
    @Override
    public FluidTanks getOutputTanks() {
        return new FluidTanks(Arrays.stream(outputs).filter(t -> t.getOutputTanks() != null).flatMap(t -> Arrays.stream(t.getOutputTanks().getBackingTanks())).collect(Collectors.toList()));
    }

    @Override
    public int getTanks() {
        return Arrays.stream(inputs).mapToInt(MachineFluidHandler::getTanks).sum() + Arrays.stream(outputs).mapToInt(MachineFluidHandler::getTanks).sum();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank < INPUT_END) return INPUT_TO_HANDLER.get(tank).getFluidInTank(tank - INPUT_START.get(INPUT_TO_HANDLER.get(tank)));
        return OUTPUT_TO_HANDLER.get(tank).getFluidInTank(tank - OUTPUT_START.get(OUTPUT_TO_HANDLER.get(tank)));
    }

    public void invalidate() {
        inputs = new MachineFluidHandler[0];
        outputs = new MachineFluidHandler[0];
    }

    public void onStructureBuild() {
        cacheInputs();
        cacheOutputs();
    }
}
