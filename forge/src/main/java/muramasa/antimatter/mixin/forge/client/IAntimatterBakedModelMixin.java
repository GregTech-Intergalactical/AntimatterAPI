package muramasa.antimatter.mixin.forge.client;

import muramasa.antimatter.client.baked.IAntimatterBakedModel;
import muramasa.antimatter.client.forge.AntimatterModelProperties;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(IAntimatterBakedModel.class)
public interface IAntimatterBakedModelMixin extends IDynamicBakedModel {
    @Shadow
    boolean hasOnlyGeneralQuads();

    @Shadow
    List<BakedQuad> getQuads(BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos);

    @Shadow
    TextureAtlasSprite getParticleIcon(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos);

    @NotNull
    @Override
    default List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, RenderType type){
        BlockAndTintGetter world = data.get(AntimatterModelProperties.WORLD);
        BlockPos pos = data.get(AntimatterModelProperties.POS);
        if (world == null || pos == null) return Collections.emptyList();
        return getQuads(state, side, rand, world, pos);
    }

    @NotNull
    @Override
    default ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        ModelData d = IDynamicBakedModel.super.getModelData(level, pos, state, modelData);
        if (d == ModelData.EMPTY) {
            var builder = ModelData.builder();
            builder.with(AntimatterModelProperties.WORLD, level);
            builder.with(AntimatterModelProperties.POS, pos);
            d = builder.build();
        }

        return d;
    }

    @Override
    default TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        BlockAndTintGetter world = data.get(AntimatterModelProperties.WORLD);
        BlockPos pos = data.get(AntimatterModelProperties.POS);
        if (world == null || pos == null) return IDynamicBakedModel.super.getParticleIcon(data);
        return getParticleIcon(world, pos);
    }
}
