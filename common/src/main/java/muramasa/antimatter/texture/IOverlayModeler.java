package muramasa.antimatter.texture;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public interface IOverlayModeler {
    ResourceLocation getOverlayModel(Machine<?> type, MachineState state);

    IOverlayModeler defaultOverride = (a,s) -> new ResourceLocation(a.getDomain(), "block/machine/overlay/" + a.getId());
}

