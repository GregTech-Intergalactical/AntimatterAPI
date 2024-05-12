package muramasa.antimatter.mixin.fabric.client;

import muramasa.antimatter.client.baked.IAntimatterBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Mixin(IAntimatterBakedModel.class)
public interface IAntimatterBakedModelMixin extends FabricBakedModel, BakedModel {

    @Shadow
    boolean hasOnlyGeneralQuads();

    @Shadow
    List<BakedQuad> getBlockQuads(BlockState state, @org.jetbrains.annotations.Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos);

    @Override
    default boolean isVanillaAdapter() {
        return false;
    }

    @Override
    default void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context){
        IAntimatterBakedModel ref = (IAntimatterBakedModel) this;
        context.bakedModelConsumer().accept(new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
                return ref.getQuads(state, side, rand, blockView, pos);
            }

            @Override
            public boolean useAmbientOcclusion() {
                return ref.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d() {
                return ref.isGui3d();
            }

            @Override
            public boolean usesBlockLight() {
                return ref.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer() {
                return ref.isCustomRenderer();
            }

            @Override
            public TextureAtlasSprite getParticleIcon() {
                return ref.getParticleIcon(blockView, pos);
            }

            @Override
            public ItemTransforms getTransforms() {
                return ref.getTransforms();
            }

            @Override
            public ItemOverrides getOverrides() {
                return ref.getOverrides();
            }
        }, state);
    }

    @Override
    default void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context){
        context.bakedModelConsumer().accept(this);
    }
}
