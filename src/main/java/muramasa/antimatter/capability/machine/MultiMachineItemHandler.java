package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.item.MultiTrackedItemHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MultiMachineItemHandler extends MachineItemHandler<TileEntityMultiMachine> {

    Optional<IItemHandlerModifiable> inputs = Optional.empty();
    Optional<IItemHandlerModifiable> outputs = Optional.empty();
    public MultiMachineItemHandler(TileEntityMachine tile) {
        //TODO: Won't work otherwise, requires TEM tile as argument to this constructor. Not sure why! Feel free to fix, this works thoguh
        super((TileEntityMultiMachine)tile);
    }

    @Override
    public IItemHandlerModifiable getInputHandler() {
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

    private IItemHandlerModifiable calculateInputs() {
        List<IItemHandlerModifiable> handlers = tile.getComponents("hatch_item_input").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getInputHandler)).map(Optional::get).collect(Collectors.toList());//this::allocateExtraSize);
        handlers.add(super.getInputHandler());
        return new MultiTrackedItemHandler(handlers.toArray(new IItemHandlerModifiable[0]));
    }

    private IItemHandlerModifiable calculateOutputs() {
        List<IItemHandlerModifiable> handlers = tile.getComponents("hatch_item_output").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getOutputHandler)).map(Optional::get).collect(Collectors.toList());//this::allocateExtraSize);
        handlers.add(super.getOutputHandler());
        return new MultiTrackedItemHandler(handlers.toArray(new IItemHandlerModifiable[0]));
    }

    @Override
    public IItemHandlerModifiable getOutputHandler() {
        return outputs.orElseGet(this::calculateOutputs);
    }
}
