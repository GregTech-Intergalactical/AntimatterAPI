package muramasa.antimatter.texture;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public interface IOverlayModeler {
    public ResourceLocation getOverlayModel(Machine<?> type, Direction side);

    public static IOverlayModeler defaultOverride = (a,d) -> new ResourceLocation(a.getDomain(), "block/machine/overlay/" + a.getId() + "/" + d.getSerializedName());;
}
