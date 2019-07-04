package muramasa.gtu.api.structure;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.int3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public class ComponentElement extends StructureElement {

    private IGregTechObject[] objects;

    public ComponentElement(String elementId, IGregTechObject... objects) {
        super(elementId);
        this.objects = objects;
    }

    public ComponentElement(IGregTechObject... objects) {
        this.objects = objects;
    }

    @Override
    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        TileEntity tile = Utils.getTile(machine.getWorld(), pos.asBP());
        if (tile instanceof IComponent) {
            IComponentHandler component = ((IComponent) tile).getComponentHandler();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i].getId().equals(component.getId())) {
                    result.addComponent(elementId, component);
                    return true;
                }
            }
            result.withError("Expected: '" + elementId + "' Found: '" + component.getId() + "' @" + pos);
            return false;
        }
        IBlockState state = machine.getWorld().getBlockState(pos.asBP());
        if (state.getBlock() instanceof IGregTechObject) {
            for (int i = 0; i < objects.length; i++) {
                if (objects[i].getId().equals(((IGregTechObject) state.getBlock()).getId())) {
                    result.addState(((IGregTechObject) state.getBlock()).getId(), pos.asBP(), state);
                    return true;
                }
            }
        }
        result.withError("No valid component found @" + pos);
        return false;
    }
}
