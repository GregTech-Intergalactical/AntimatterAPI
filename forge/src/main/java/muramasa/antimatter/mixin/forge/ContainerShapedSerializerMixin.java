package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.recipe.container.ContainerItemShapedRecipe;
import muramasa.antimatter.recipe.container.ContainerItemShapedRecipe.Serializer;
import muramasa.antimatter.recipe.material.MaterialSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Serializer.class)
public class ContainerShapedSerializerMixin implements IForgeRegistryEntry<Serializer> {
    @Unique
    private ResourceLocation registryName = null;

    @Override
    public Serializer setRegistryName(ResourceLocation arg) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + arg.toString() + " Old: " + getRegistryName());
        this.registryName = arg;
        return (Serializer) (Object)this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public Class<Serializer> getRegistryType() {
        return Serializer.class;
    }
}
