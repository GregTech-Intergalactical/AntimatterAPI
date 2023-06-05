package muramasa.antimatter.client.baked;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class AntimatterBakedModel<T> implements IAntimatterBakedModel {

    protected TextureAtlasSprite particle;
    protected boolean onlyGeneralQuads = false; //If the model only has "general quads", like pipes

    public AntimatterBakedModel(TextureAtlasSprite p) {
        this.particle = Objects.requireNonNull(p, "Missing particle texture in AntimatterBakedModel");
    }


    public void onlyGeneralQuads() {
        this.onlyGeneralQuads = true;
    }

    @Override
    public boolean hasOnlyGeneralQuads() {
        return onlyGeneralQuads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getParticleIcon(EmptyModelData.INSTANCE);
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
        return particle;
    }
}
