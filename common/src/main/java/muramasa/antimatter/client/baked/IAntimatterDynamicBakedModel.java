package muramasa.antimatter.client.baked;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.modeldata.AntimatterEmptyModelData;
import muramasa.antimatter.client.modeldata.IAntimatterModelData;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface IAntimatterDynamicBakedModel extends BakedModel {
    @Override
    default @Nonnull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand)
    {
        return getQuads(state, side, rand, AntimatterEmptyModelData.INSTANCE);
    }

    // Force this to be overriden otherwise this introduces a default cycle between the two overloads.
    @Nonnull
    List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IAntimatterModelData extraData);

    @Override
    default ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }

    default boolean useAmbientOcclusion(BlockState state) { return useAmbientOcclusion(); }

    default @Nonnull IAntimatterModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IAntimatterModelData modelData)
    {
        return modelData;
    }

    default TextureAtlasSprite getParticleIcon(@Nonnull IAntimatterModelData data)
    {
        return getParticleIcon();
    }

    /**
     * Override to true, to tell forge to call the getLayerModels method below.
     */
    default boolean isLayered()
    {
        return false;
    }

    /**
     * If {@see isLayered()} returns true, this is called to get the list of layers to draw.
     */
    default List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous)
    {
        return Collections.singletonList(Pair.of(this, ItemBlockRenderTypes.getRenderType(itemStack, fabulous)));
    }
}
