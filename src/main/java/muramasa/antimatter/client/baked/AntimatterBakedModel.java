package muramasa.antimatter.client.baked;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AntimatterBakedModel<T> implements IDynamicBakedModel {

    protected IBakedModel bakedModel;
    protected TextureAtlasSprite particle;
    protected boolean onlyGeneralQuads; //If the model only has "general quads", like pipes

    public AntimatterBakedModel() {
        //TODO set error sprite
    }

    public AntimatterBakedModel(IBakedModel bakedModel) {
        this();
        this.bakedModel = bakedModel;
    }

    public T particle(TextureAtlasSprite p) {
        if (particle == null) this.particle = p;
        return (T) this;
    }


    public T onlyGeneralQuads() {
        this.onlyGeneralQuads = true;
        return (T) this;
    }

    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return bakedModel != null ? bakedModel.getQuads(state, side, rand, data) : Collections.emptyList();
    }

    public List<BakedQuad> getItemQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return bakedModel != null ? bakedModel.getQuads(null, side, rand, data) : Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        try {
            if (onlyGeneralQuads && side != null) return Collections.emptyList();
            return state != null ? getBlockQuads(state, side, rand, data) : getItemQuads(side, rand, data);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isAmbientOcclusion() {
        return bakedModel == null || bakedModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return bakedModel == null || bakedModel.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return bakedModel == null || bakedModel.isSideLit();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return bakedModel == null || bakedModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getParticleTexture(EmptyModelData.INSTANCE);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return bakedModel != null ? bakedModel.getItemCameraTransforms() : ItemCameraTransforms.DEFAULT;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        return bakedModel != null ? bakedModel.handlePerspective(cameraTransformType, mat) : net.minecraftforge.client.ForgeHooksClient.handlePerspective(getBakedModel(), cameraTransformType, mat);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return bakedModel != null ? bakedModel.getOverrides() : ItemOverrideList.EMPTY;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        return particle;
    }
}
