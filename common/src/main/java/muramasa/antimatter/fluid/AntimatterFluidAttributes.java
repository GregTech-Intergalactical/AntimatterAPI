package muramasa.antimatter.fluid;

import muramasa.antimatter.Antimatter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class AntimatterFluidAttributes {
    private String translationKey;
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    @Nullable
    private final ResourceLocation overlayTexture;
    private final SoundEvent fillSound;
    private final SoundEvent emptySound;
    private final int luminosity;
    private final int density;
    private final int temperature;
    private final int viscosity;
    private final boolean isGaseous;
    private final Rarity rarity;
    private final int color;

    protected AntimatterFluidAttributes(AntimatterFluidAttributes.Builder builder, AntimatterFluid fluid) {
        this.translationKey = builder.translationKey != null ? builder.translationKey : Util.makeDescriptionId("fluid", fluid.getLoc());
        this.stillTexture = builder.stillTexture;
        this.flowingTexture = builder.flowingTexture;
        this.overlayTexture = builder.overlayTexture;
        this.color = builder.color;
        this.fillSound = builder.fillSound;
        this.emptySound = builder.emptySound;
        this.luminosity = builder.luminosity;
        this.temperature = builder.temperature;
        this.viscosity = builder.viscosity;
        this.density = builder.density;
        this.isGaseous = builder.isGaseous;
        this.rarity = builder.rarity;
    }

    public ItemStack getBucket(FluidStack stack) {
        return new ItemStack(stack.getFluid().getBucket());
    }

    public BlockState getBlock(BlockAndTintGetter reader, BlockPos pos, FluidState state) {
        return state.createLegacyBlock();
    }

    public FluidState getStateForPlacement(BlockAndTintGetter reader, BlockPos pos, FluidStack state) {
        return state.getFluid().defaultFluidState();
    }

    public final boolean canBePlacedInWorld(BlockAndTintGetter reader, BlockPos pos, FluidState state) {
        return !this.getBlock(reader, pos, state).isAir();
    }

    public final boolean canBePlacedInWorld(BlockAndTintGetter reader, BlockPos pos, FluidStack state) {
        return !this.getBlock(reader, pos, this.getStateForPlacement(reader, pos, state)).isAir();
    }

    public final boolean isLighterThanAir() {
        return this.density <= 0;
    }

    public Component getDisplayName(FluidStack stack) {
        return new TranslatableComponent(this.getTranslationKey());
    }

    public String getTranslationKey(FluidStack stack) {
        return this.getTranslationKey();
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public final int getLuminosity() {
        return this.luminosity;
    }

    public final int getDensity() {
        return this.density;
    }

    public final int getTemperature() {
        return this.temperature;
    }

    public final int getViscosity() {
        return this.viscosity;
    }

    public final boolean isGaseous() {
        return this.isGaseous;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public int getColor() {
        return this.color;
    }

    public ResourceLocation getStillTexture() {
        return this.stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return this.flowingTexture;
    }

    @Nullable
    public ResourceLocation getOverlayTexture() {
        return this.overlayTexture;
    }

    public SoundEvent getFillSound() {
        return this.fillSound;
    }

    public SoundEvent getEmptySound() {
        return this.emptySound;
    }


    public static AntimatterFluidAttributes.Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return new AntimatterFluidAttributes.Builder(stillTexture, flowingTexture, AntimatterFluidAttributes::new);
    }

    public Stream<ResourceLocation> getTextures() {
        return this.overlayTexture != null ? Stream.of(this.stillTexture, this.flowingTexture, this.overlayTexture) : Stream.of(this.stillTexture, this.flowingTexture);
    }

    public static class Builder {
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;
        private ResourceLocation overlayTexture;
        private int color = -1;
        private String translationKey;
        private SoundEvent fillSound;
        private SoundEvent emptySound;
        private int luminosity = 0;
        private int density = 1000;
        private int temperature = 300;
        private int viscosity = 1000;
        private boolean isGaseous;
        private Rarity rarity;
        private BiFunction<AntimatterFluidAttributes.Builder, AntimatterFluid, AntimatterFluidAttributes> factory;

        protected Builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<AntimatterFluidAttributes.Builder, AntimatterFluid, AntimatterFluidAttributes> factory) {
            this.rarity = Rarity.COMMON;
            this.factory = factory;
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        public final AntimatterFluidAttributes.Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public final AntimatterFluidAttributes.Builder color(int color) {
            this.color = color;
            return this;
        }

        public final AntimatterFluidAttributes.Builder overlay(ResourceLocation texture) {
            this.overlayTexture = texture;
            return this;
        }

        public final AntimatterFluidAttributes.Builder luminosity(int luminosity) {
            this.luminosity = luminosity;
            return this;
        }

        public final AntimatterFluidAttributes.Builder density(int density) {
            this.density = density;
            return this;
        }

        public final AntimatterFluidAttributes.Builder temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public final AntimatterFluidAttributes.Builder viscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public final AntimatterFluidAttributes.Builder gaseous() {
            this.isGaseous = true;
            return this;
        }

        public final AntimatterFluidAttributes.Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public final AntimatterFluidAttributes.Builder sound(SoundEvent sound) {
            this.fillSound = this.emptySound = sound;
            return this;
        }

        public final AntimatterFluidAttributes.Builder sound(SoundEvent fillSound, SoundEvent emptySound) {
            this.fillSound = fillSound;
            this.emptySound = emptySound;
            return this;
        }

        public AntimatterFluidAttributes build(AntimatterFluid fluid) {
            return this.factory.apply(this, fluid);
        }
    }
}
