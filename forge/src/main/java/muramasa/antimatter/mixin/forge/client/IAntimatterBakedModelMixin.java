package muramasa.antimatter.mixin.forge.client;

import muramasa.antimatter.client.baked.IAntimatterBakedModel;
import muramasa.antimatter.client.forge.AntimatterModelProperties;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(IAntimatterBakedModel.class)
public interface IAntimatterBakedModelMixin extends IDynamicBakedModel {
    @Shadow
    boolean hasOnlyGeneralQuads();

    @Shadow
    List<BakedQuad> getQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos);

    @Shadow
    TextureAtlasSprite getParticleIcon(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos);

    @NotNull
    @Override
    default List<BakedQuad> getQuads(@javax.annotation.Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data){
        BlockAndTintGetter world = data.getData(AntimatterModelProperties.WORLD);
        BlockPos pos = data.getData(AntimatterModelProperties.POS);
        if (world == null || pos == null) return Collections.emptyList();
        return getQuads(state, side, rand, world, pos);
    }

    @NotNull
    @Override
    default IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData modelData) {
        IModelData d = IDynamicBakedModel.super.getModelData(level, pos, state, modelData);
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
