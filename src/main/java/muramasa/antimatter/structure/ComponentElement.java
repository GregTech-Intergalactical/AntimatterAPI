package muramasa.antimatter.structure;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.util.int3;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;

public class ComponentElement extends StructureElement {

    private IAntimatterObject[] objects;

    public ComponentElement(String elementId, IAntimatterObject... objects) {
        super(elementId);
        this.objects = objects;
    }

    public ComponentElement(IAntimatterObject... objects) {
        this.objects = objects;
    }

    @Override
    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        TileEntity tile = Utils.getTile(machine.getWorld(), pos);
        if (tile instanceof IComponent) {
            if (((IComponent) tile).getComponentHandler().isPresent()) {
                IComponentHandler component = ((IComponent) tile).getComponentHandler().get();
                for (int i = 0; i < objects.length; i++) {
                    if (objects[i].getId().equals(component.getId())) {
                        result.addComponent(elementId, component);
                        return true;
                    }
                }
                result.withError("Expected: '" + elementId + "' Found: '" + component.getId() + "' @" + pos);
                return false;
            }
        }
        BlockState state = machine.getWorld().getBlockState(pos);
        if (state.getBlock() instanceof IAntimatterObject) {
            for (int i = 0; i < objects.length; i++) {
                if (objects[i].getId().equals(((IAntimatterObject) state.getBlock()).getId())) {
                    result.addState(((IAntimatterObject) state.getBlock()).getId(), pos, state);
                    return true;
                }
            }
        }
        result.withError("No valid component found @" + pos);
        return false;
    }
}
