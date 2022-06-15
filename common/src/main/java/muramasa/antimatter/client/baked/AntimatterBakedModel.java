package muramasa.antimatter.client.baked;

import muramasa.antimatter.client.modeldata.AntimatterEmptyModelData;
import muramasa.antimatter.client.modeldata.IAntimatterModelData;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class AntimatterBakedModel<T> implements IAntimatterDynamicBakedModel {

    protected TextureAtlasSprite particle;
    protected boolean onlyGeneralQuads = false; //If the model only has "general quads", like pipes

    public AntimatterBakedModel(TextureAtlasSprite p) {
        this.particle = Objects.requireNonNull(p, "Missing particle texture in AntimatterBakedModel");
    }


    public void onlyGeneralQuads() {
        this.onlyGeneralQuads = true;
    }

    public abstract List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IAntimatterModelData data);

    public abstract List<BakedQuad> getItemQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IAntimatterModelData data);

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IAntimatterModelData data) {
        try {
            if (onlyGeneralQuads && side != null) return Collections.emptyList();
            return state != null ? getBlockQuads(state, side, rand, data) : getItemQuads(side, rand, data);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getParticleIcon(AntimatterEmptyModelData.INSTANCE);
    }

    /*@Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return bakedModel != null ? bakedModel.getItemCameraTransforms() : ItemCameraTransforms.DEFAULT;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        return bakedModel != null ? bakedModel.handlePerspective(cameraTransformType, mat) : net.minecraftforge.client.ForgeHooksClient.handlePerspective(getBakedModel(), cameraTransformType, mat);
    }*/


    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IAntimatterModelData data) {
        return particle;
    }
}
