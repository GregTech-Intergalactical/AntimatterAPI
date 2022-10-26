package muramasa.antimatter.datagen.builder;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface IModelLocation {
    ResourceLocation getLocation();
}
