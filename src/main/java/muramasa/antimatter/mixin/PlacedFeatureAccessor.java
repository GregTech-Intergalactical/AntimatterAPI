package muramasa.antimatter.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@Mixin(PlacedFeature.class)
public interface PlacedFeatureAccessor {
    @Accessor
    Supplier<ConfiguredFeature<?, ?>> getFeature();
}
