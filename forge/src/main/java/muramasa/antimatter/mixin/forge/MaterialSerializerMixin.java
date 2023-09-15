package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.recipe.material.MaterialSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MaterialSerializer.class, remap = false)
public class MaterialSerializerMixin implements IForgeRegistryEntry<MaterialSerializer> {
    @Unique
    private ResourceLocation registryName = null;

    @Override
    public MaterialSerializer setRegistryName(ResourceLocation arg) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + arg.toString() + " Old: " + getRegistryName());
        this.registryName = arg;
        return (MaterialSerializer) (Object)this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public Class<MaterialSerializer> getRegistryType() {
        return MaterialSerializer.class;
    }
}
