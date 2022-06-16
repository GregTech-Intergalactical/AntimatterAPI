/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

/**
 * Convenience interface with default implementation of {@link BakedModel#getQuads(BlockState, Direction, Random, IModelData)}.
 */
public interface IDynamicBakedModel extends BakedModel, FabricBakedModel
{
    @Override
    default boolean isVanillaAdapter(){
        return false;
    }

    @Override
    default @Nonnull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Override
    default ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }
    
    // Force this to be overriden otherwise this introduces a default cycle between the two overloads.
    @Override
    @Nonnull
    List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData);

    //todo figure this out
    @Override
    default void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context){
        IModelData data = getModelData(blockView, pos, state, EmptyModelData.INSTANCE);
        List<BakedQuad> quads = getQuads(state, context.getEmitter().cullFace(), randomSupplier.get(), data);
    }

    @Override
    default void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context){
        context.fallbackConsumer().accept(this);
    }
}
