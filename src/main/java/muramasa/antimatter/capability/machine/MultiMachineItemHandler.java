package muramasa.antimatter.capability.machine;

import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

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
        if (inputs.isPresent()) return inputs.get();
        List<IItemHandlerModifiable> list = tile.getComponents("hatch_item_input").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getInputHandler)).map(LazyHolder::get).collect(Collectors.toList());
        if (tile instanceof TileEntityBasicMultiMachine){
            list.add(this.inventories.get(MachineFlag.ITEM_INPUT));
        }
        IItemHandlerModifiable[] handlers = list.toArray(new IItemHandlerModifiable[0]);//this::allocateExtraSize);
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
        List<IItemHandlerModifiable> list = tile.getComponents("hatch_item_input").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getInputHandler)).map(LazyHolder::get).collect(Collectors.toList());
        if (tile instanceof TileEntityBasicMultiMachine){
            list.add(this.inventories.get(MachineFlag.ITEM_INPUT));
        }
        IItemHandlerModifiable[] handlers = list.toArray(new IItemHandlerModifiable[0]);//this::allocateExtraSize);
       // handlers[handlers.length-1] = this.inputWrapper;
        inputs = Optional.of(new CombinedInvWrapper(handlers));

        List<IItemHandlerModifiable> l = tile.getComponents("output").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getOutputHandler)).map(LazyHolder::get).collect(Collectors.toList());
        if (tile instanceof TileEntityBasicMultiMachine){
            l.add(this.inventories.get(MachineFlag.ITEM_OUTPUT));
        }
        IItemHandlerModifiable[] h = l.toArray(new IItemHandlerModifiable[0]);//this::allocateExtraSize);
        //h[handlers.length-1] = this.outputWrapper;
        outputs = Optional.of(new CombinedInvWrapper(h));
    }


    @Override
    public IItemHandlerModifiable getOutputHandler() {
        if (outputs.isPresent()) return outputs.get();
        List<IItemHandlerModifiable> list = tile.getComponents("output").stream().filter(t -> t.getItemHandler().isPresent()).map(t -> t.getItemHandler().map(MachineItemHandler::getOutputHandler)).map(LazyHolder::get).collect(Collectors.toList());
        if (tile instanceof TileEntityBasicMultiMachine){
            list.add(this.inventories.get(MachineFlag.ITEM_OUTPUT));
        }
        IItemHandlerModifiable[] handlers = list.toArray(new IItemHandlerModifiable[0]);//this::allocateExtraSize);
        //handlers[handlers.length-1] = this.outputWrapper;
        outputs = Optional.of(new CombinedInvWrapper(handlers));
        return outputs.get();
    }
}
