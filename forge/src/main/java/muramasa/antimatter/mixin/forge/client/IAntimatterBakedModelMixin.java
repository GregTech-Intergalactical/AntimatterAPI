package muramasa.antimatter.mixin.forge.client;

import muramasa.antimatter.client.baked.IAntimatterBakedModel;
import muramasa.antimatter.client.forge.AntimatterModelProperties;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
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
    List<BakedQuad> getQuads(BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos);

    @Shadow
    TextureAtlasSprite getParticleIcon(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos);

    @NotNull
    @Override
    default List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData data){
        BlockAndTintGetter world = data.getData(AntimatterModelProperties.WORLD);
        BlockPos pos = data.getData(AntimatterModelProperties.POS);
        if (world == null || pos == null) return Collections.emptyList();
        return getQuads(state, side, rand, world, pos);
    }

    @NotNull
    @Override
    default IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData modelData) {
        IModelData d = IDynamicBakedModel.super.getModelData(level, pos, state, modelData);
        if (d == EmptyModelData.INSTANCE) d = new ModelDataMap.Builder().build();
        d.setData(AntimatterModelProperties.WORLD, level);
        d.setData(AntimatterModelProperties.POS, pos);
        return d;
    }

    @Override
    default TextureAtlasSprite getParticleIcon(@NotNull IModelData data) {
        BlockAndTintGetter world = data.getData(AntimatterModelProperties.WORLD);
        BlockPos pos = data.getData(AntimatterModelProperties.POS);
        if (world == null || pos == null) return IDynamicBakedModel.super.getParticleIcon(data);
        return getParticleIcon(world, pos);
    }
}
