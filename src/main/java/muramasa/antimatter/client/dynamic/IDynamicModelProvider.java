package muramasa.antimatter.client.dynamic;

import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public interface IDynamicModelProvider extends IAntimatterObject {
    ResourceLocation getModel(String type, Direction dir);
}
