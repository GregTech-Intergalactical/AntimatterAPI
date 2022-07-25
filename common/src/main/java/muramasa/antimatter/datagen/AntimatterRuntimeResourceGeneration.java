package muramasa.antimatter.datagen;

import muramasa.antimatter.Ref;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.resources.ResourceLocation;

public class AntimatterRuntimeResourceGeneration {
    public static final RuntimeResourcePack DYNAMIC_RESOURCE_PACK = RuntimeResourcePack.create(new ResourceLocation(Ref.ID, "dynamic"));

    public static ResourceLocation getLootLoc(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "loot_tables/blocks/" + id.getPath());
    }

    public static ResourceLocation getStateLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "blockstates/", registryId.getPath()));
    }

    public static ResourceLocation getModelLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "models/", registryId.getPath()));
    }

    public static ResourceLocation getRecipeLog(ResourceLocation recipeId) {
        return new ResourceLocation(recipeId.getNamespace(), String.join("", "recipes/", recipeId.getPath()));
    }

    public static ResourceLocation getTagLoc(String identifier, ResourceLocation tagId) {
        return new ResourceLocation(tagId.getNamespace(), String.join("", identifier, "/", tagId.getPath()));
    }
}
