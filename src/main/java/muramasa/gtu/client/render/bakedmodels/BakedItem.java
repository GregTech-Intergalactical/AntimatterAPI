package muramasa.gtu.client.render.bakedmodels;

import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Random;

public class BakedItem extends BakedBase {

    //TODO remove need for this by using json model templates with the correct transforms
    private IBakedModel baked;

    public BakedItem(IBakedModel baked) {
        this.baked = baked;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return baked.getQuads(state, side, rand, data);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of(this, ModelUtils.getItemTransform(cameraTransformType));
    }
}
