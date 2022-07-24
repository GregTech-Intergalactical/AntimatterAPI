package muramasa.antimatter.datagen;

import muramasa.antimatter.Ref;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.resources.ResourceLocation;

public class AntimatterRuntimeResourceGeneration {
    public static final RuntimeResourcePack DYNAMIC_RESOURCE_PACK = RuntimeResourcePack.create(new ResourceLocation(Ref.ID, "dynamic"));

    public static ResourceLocation getTagLoc(String identifier, ResourceLocation tagId) {
        return new ResourceLocation(tagId.getNamespace(), String.join("", "tags/", identifier, "/", tagId.getPath(), ".json"));
    }
}
