package muramasa.antimatter.structure;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

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
    public boolean evaluate(TileEntityBasicMultiMachine<?> machine, int3 pos, StructureResult result) {
        TileEntity tile = machine.getLevel().getBlockEntity(pos);
        if (tile instanceof IComponent) {
            if (((IComponent) tile).getComponentHandler().isPresent()) {
                IComponentHandler component = ((IComponent) tile).getComponentHandler().orElse(null);
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
        BlockState state = machine.getLevel().getBlockState(pos);
        if (state.getBlock() instanceof IAntimatterObject) {
            for (int i = 0; i < objects.length; i++) {
                if (objects[i].getId().equals(((IAntimatterObject) state.getBlock()).getId())) {
                    if (!elementId.isEmpty()) {
                        result.addState(elementId, pos, state);
                    } else {
                        result.addState(((IAntimatterObject) state.getBlock()).getId(), pos, state);
                    }
                    return true;
                }
            }
        }
        result.withError("No valid component found @" + pos);
        return false;
    }

    @Override
    public boolean renderShared() {
        return true;
    }

    @Override
    public void onInfoTooltip(List<ITextComponent> text, long count, TileEntityBasicMultiMachine<?> machine) {
        super.onInfoTooltip(text, count, machine);
        if (count > 0) text.add(new StringTextComponent("Can be in multiple positions."));
    }
}
