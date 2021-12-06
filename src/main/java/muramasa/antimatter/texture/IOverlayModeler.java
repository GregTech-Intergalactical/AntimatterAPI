package muramasa.antimatter.texture;

import muramasa.antimatter.machine.types.Machine;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public interface IOverlayModeler {
    public ResourceLocation getOverlayModel(Machine<?> type, Direction side);

    public static IOverlayModeler defaultOverride = (a,d) -> new ResourceLocation(a.getDomain(), "block/machine/overlay/" + a.getId() + "/" + d.getSerializedName());;
}
