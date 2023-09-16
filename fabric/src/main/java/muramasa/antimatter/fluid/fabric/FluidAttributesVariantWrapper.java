package muramasa.antimatter.fluid.fabric;

import earth.terrarium.botarium.common.registry.fluid.FluidProperties;
import muramasa.antimatter.util.Utils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("ALL")
public record FluidAttributesVariantWrapper(FluidProperties attributes) implements FluidVariantAttributeHandler {
    @Override
    public Component getName(FluidVariant fluidVariant) {
        return Utils.translatable(Util.makeDescriptionId("fluid_type", attributes.id()));
    }

    @Override
    public int getLuminance(FluidVariant variant) {
        return attributes.lightLevel();
    }

    @Override
    public int getTemperature(FluidVariant variant) {
        return attributes.temperature();
    }

    @Override
    public int getViscosity(FluidVariant variant, @Nullable Level world) {
        return attributes.viscosity();
    }

    @Override
    public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
        return Optional.of(attributes.sounds().getSound("bucket_empty"));
    }

    @Override
    public Optional<SoundEvent> getFillSound(FluidVariant variant) {
        return Optional.of(attributes.sounds().getSound("bucket_fill"));
    }
}
