package muramasa.antimatter.client.dynamic;

import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public interface IDynamicModelProvider extends IAntimatterObject {
    ResourceLocation getModel(String type, Direction dir);
}
