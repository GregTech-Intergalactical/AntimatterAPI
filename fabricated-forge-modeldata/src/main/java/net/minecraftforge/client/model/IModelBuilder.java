/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraftforge.mixin.client.SimpleBakedModelBuilderAccessor;

public interface IModelBuilder<T extends IModelBuilder<T>>
{
    static IModelBuilder<?> of(IModelConfiguration owner, ItemOverrides overrides, TextureAtlasSprite particle)
    {
        return new Simple(SimpleBakedModelBuilderAccessor.invokeInit(owner.useSmoothLighting(), owner.isSideLit(), owner.isShadedInGui(), owner.getCameraTransforms(), overrides).particle(particle));
    }

    T addFaceQuad(Direction facing, BakedQuad quad);
    T addGeneralQuad(BakedQuad quad);

    BakedModel build();

    class Simple implements IModelBuilder<Simple> {
        final SimpleBakedModel.Builder builder;

        Simple(SimpleBakedModel.Builder builder)
        {
            this.builder = builder;
        }

        @Override
        public Simple addFaceQuad(Direction facing, BakedQuad quad)
        {
            builder.addCulledFace(facing, quad);
            return this;
        }

        @Override
        public Simple addGeneralQuad(BakedQuad quad)
        {
            builder.addUnculledFace(quad);
            return this;
        }

        @Override
        public BakedModel build()
        {
            return builder.build();
        }
    }
}

