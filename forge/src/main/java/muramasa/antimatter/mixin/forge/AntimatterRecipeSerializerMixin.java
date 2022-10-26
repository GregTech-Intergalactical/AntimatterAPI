package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.recipe.serializer.AntimatterRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = AntimatterRecipeSerializer.class, remap = false)
public class AntimatterRecipeSerializerMixin implements IForgeRegistryEntry<AntimatterRecipeSerializer> {
    @Unique
    private ResourceLocation registryName = null;

    @Override
    public AntimatterRecipeSerializer setRegistryName(ResourceLocation arg) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + arg.toString() + " Old: " + getRegistryName());
        this.registryName = arg;
        return (AntimatterRecipeSerializer) (Object)this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public Class<AntimatterRecipeSerializer> getRegistryType() {
        return AntimatterRecipeSerializer.class;
    }
}
