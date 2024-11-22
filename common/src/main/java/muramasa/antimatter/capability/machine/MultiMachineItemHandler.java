package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.item.ITrackedHandler;
import muramasa.antimatter.capability.item.MultiTrackedItemHandler;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MultiMachineItemHandler<T extends BlockEntityMultiMachine<T>> extends MachineItemHandler<T> {

    Optional<ITrackedHandler> inputs = Optional.empty();
    Optional<ITrackedHandler> outputs = Optional.empty();

    public MultiMachineItemHandler(T tile) {
        super(tile);
    }

    @Override
    public boolean canOutputsFit(ItemStack[] a) {
        return outputs.isPresent() && a != null && super.canOutputsFit(a);
    }

    @Override
    public ITrackedHandler getInputHandler() {
        return inputs.orElseGet(this::calculateInputs);
    }

    public void invalidate() {
        inputs = Optional.empty();
        outputs = Optional.empty();
    }

    public void onStructureBuild() {
        inputs = Optional.of(calculateInputs());
        outputs = Optional.of(calculateOutputs());
    }

    private ITrackedHandler calculateInputs() {
        List<ExtendedItemContainer> handlers = tile.getComponentsByHandlerId(inputComponentString()).stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().get()).sorted(this::compareInputBuses).map(MachineItemHandler::getInputHandler).collect(Collectors.toList());
        handlers.add(super.getInputHandler());
        return new MultiTrackedItemHandler(handlers.toArray(new ExtendedItemContainer[0]));
    }

    protected int compareInputBuses(MachineItemHandler<?> a, MachineItemHandler<?> b) {
        return 0;
    }

    protected String inputComponentString(){
        return "item_input";
    }

    protected String outputComponentString(){
        return "item_output";
    }

    protected int compareOutputBuses(MachineItemHandler<?> a, MachineItemHandler<?> b) {
        return 0;
    }

    private ITrackedHandler calculateOutputs() {
        List<ExtendedItemContainer> handlers = tile.getComponentsByHandlerId(outputComponentString()).stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().get()).sorted(this::compareOutputBuses).map(MachineItemHandler::getOutputHandler).collect(Collectors.toList());
        handlers.add(super.getOutputHandler());
        return new MultiTrackedItemHandler(handlers.toArray(new ExtendedItemContainer[0]));
    }

    @Override
    public ITrackedHandler getOutputHandler() {
        return outputs.orElseGet(this::calculateOutputs);
    }
}
