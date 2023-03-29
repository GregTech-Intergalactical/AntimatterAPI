package muramasa.antimatter.fluid.fabric;

import muramasa.antimatter.fluid.AntimatterFluidAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("ALL")
public record FluidAttributesVariantWrapper(AntimatterFluidAttributes attributes) implements FluidVariantAttributeHandler {
    @Override
    public Component getName(FluidVariant fluidVariant) {
        return attributes.getDisplayName(new FluidStack(fluidVariant, 1));
    }

    @Override
    public int getLuminance(FluidVariant variant) {
        return attributes.getLuminosity();
    }

    @Override
    public int getTemperature(FluidVariant variant) {
        return attributes.getTemperature();
    }

    @Override
    public int getViscosity(FluidVariant variant, @Nullable Level world) {
        return attributes.getViscosity();
    }

    @Override
    public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
        return Optional.of(attributes.getEmptySound());
    }

    @Override
    public Optional<SoundEvent> getFillSound(FluidVariant variant) {
        return Optional.of(attributes.getFillSound());
    }
}
