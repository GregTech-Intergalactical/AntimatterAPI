package muramasa.antimatter.capability.machine;

import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.Optional;


public class MultiMachineItemHandler extends MachineItemHandler<TileEntityMultiMachine> {

    Optional<IItemHandlerModifiable> inputs = Optional.empty();
    Optional<IItemHandlerModifiable> outputs = Optional.empty();
    public MultiMachineItemHandler(TileEntityMachine tile) {
        //TODO: Won't work otherwise, requires TEM tile as argument to this constructor. Not sure why! Feel free to fix, this works thoguh
        super((TileEntityMultiMachine)tile);
    }

    @Override
    public IItemHandlerModifiable getInputHandler() {
        if (inputs.isPresent()) return inputs.get();
        IItemHandlerModifiable[] handlers = tile.getComponents("hatch_item_input").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getInputHandler)).map(LazyHolder::get).toArray(IItemHandlerModifiable[]::new);//this::allocateExtraSize);
       // handlers[handlers.length-1] = this.inputWrapper;
        inputs = Optional.of(new CombinedInvWrapper(handlers));
        return inputs.get();
    }

    private IItemHandlerModifiable[] allocateExtraSize(int size) {
        return new IItemHandlerModifiable[size+1];
    }

    public void invalidate() {
        inputs = Optional.empty();
        outputs = Optional.empty();
    }

    public void onStructureBuild() {
        IItemHandlerModifiable[] handlers = tile.getComponents("hatch_item_input").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getInputHandler)).map(LazyHolder::get).toArray(IItemHandlerModifiable[]::new);//this::allocateExtraSize);
       // handlers[handlers.length-1] = this.inputWrapper;
        inputs = Optional.of(new CombinedInvWrapper(handlers));

        IItemHandlerModifiable[] h = tile.getComponents("hatch_item_output").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getOutputHandler)).map(LazyHolder::get).toArray(IItemHandlerModifiable[]::new);//this::allocateExtraSize);
        //h[handlers.length-1] = this.outputWrapper;
        outputs = Optional.of(new CombinedInvWrapper(h));
    }


    @Override
    public IItemHandlerModifiable getOutputHandler() {
        if (outputs.isPresent()) return outputs.get();
        IItemHandlerModifiable[] handlers = tile.getComponents("output").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getOutputHandler)).map(LazyHolder::get).toArray(IItemHandlerModifiable[]::new);//this::allocateExtraSize);
        //handlers[handlers.length-1] = this.outputWrapper;
        outputs = Optional.of(new CombinedInvWrapper(handlers));
        return outputs.get();
    }
}
