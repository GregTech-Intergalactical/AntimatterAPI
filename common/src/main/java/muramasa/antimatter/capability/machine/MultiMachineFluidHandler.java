package muramasa.antimatter.capability.machine;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class MultiMachineFluidHandler<T extends BlockEntityMultiMachine<T>> extends MachineFluidHandler<T> {

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
    public boolean canOutputsFit(FluidHolder[] outputs) {
        if (outputs != null && this.outputs != null){
            FluidHolder[] outputCopies = new FluidHolder[outputs.length];
            for (int i = 0; i < outputs.length; i++) {
                outputCopies[i] = outputs[i].copyHolder();
            }
            int filled = 0;
            for (FluidHolder outputCopy : outputCopies) {
                for (MachineFluidHandler<?> output : this.outputs) {
                    long fill = output.fillOutput(outputCopy, true);
                    if (fill > 0) {
                        outputCopy.setAmount(outputCopy.getFluidAmount() - fill);
                        if (outputCopy.getFluidAmount() <= 0) {
                            filled++;
                            break;
                        }
                    }
                }
            }
            return filled == outputs.length;
        }
        return false;
    }

    protected void cacheInputs() {
        inputs = tile.getComponentsByHandlerId(inputComponentString()).stream().map(IComponentHandler::getFluidHandler).map(Optional::get).toArray(MachineFluidHandler<?>[]::new);//this::allocateExtraSize);
        // handlers[handlers.length-1] = this.inputWrapper;
        INPUT_TO_HANDLER.clear();
        INPUT_START.clear();
        int i = 0;
        for (MachineFluidHandler<?> input : inputs) {
            for (int j = 0; j < input.getSize(); j++) {
                INPUT_TO_HANDLER.put(j + i, input);
                if (j == 0) INPUT_START.put(input, i);
            }
            i += input.getSize();
        }
        INPUT_END = i;
    }

    protected String inputComponentString(){
        return "fluid_input";
    }

    protected String outputComponentString(){
        return "fluid_output";
    }

    protected void cacheOutputs() {
        outputs = tile.getComponentsByHandlerId(outputComponentString()).stream().map(IComponentHandler::getFluidHandler).map(Optional::get).toArray(MachineFluidHandler<?>[]::new);//this::allocateExtraSize);
        // handlers[handlers.length-1] = this.inputWrapper;
        OUTPUT_TO_HANDLER.clear();
        OUTPUT_START.clear();
        int i = 0;
        for (MachineFluidHandler<?> output : outputs) {
            for (int j = 0; j < output.getSize(); j++) {
                OUTPUT_TO_HANDLER.put(j + i, output);
                if (j == 0) OUTPUT_START.put(output, i);
            }
            i += output.getSize();
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
    public int getSize() {
        return Arrays.stream(inputs).mapToInt(MachineFluidHandler::getSize).sum() + Arrays.stream(outputs).mapToInt(MachineFluidHandler::getSize).sum();
    }

    @NotNull
    @Override
    public FluidHolder getFluidInTank(int tank) {
        if (tank < INPUT_END)
            return INPUT_TO_HANDLER.get(tank).getFluidInTank(tank - INPUT_START.get(INPUT_TO_HANDLER.get(tank)));
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
