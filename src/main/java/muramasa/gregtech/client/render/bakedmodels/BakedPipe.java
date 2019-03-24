package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.client.render.ModelUtils;
import muramasa.gregtech.client.render.models.ModelPipe;
import muramasa.gregtech.client.render.overrides.ItemOverridePipe;
import muramasa.gregtech.common.blocks.pipe.BlockCable;
import muramasa.gregtech.common.blocks.pipe.BlockPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedPipe extends BakedBase {

    private static ItemOverrideList OVERRIDE = new ItemOverridePipe();
    public static HashMap<String, IBakedModel> BAKED = new HashMap<>();

    public BakedPipe(HashMap<String, IBakedModel> baked) {
        BAKED = baked;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        IExtendedBlockState exState = (IExtendedBlockState) state;
        int size = exState.getValue(BlockPipe.SIZE);

        if (size >= 0) {
            quads.addAll(BAKED.get("base_" + PipeSize.VALUES[size].getName()).getQuads(state, side, rand));
            if (ModelUtils.hasUnlistedProperty(exState, BlockCable.INSULATED) && exState.getValue(BlockCable.INSULATED) == 1) {
                ModelUtils.tex(quads, 0, new Texture(ModelPipe.CABLE));
            }
        }

        return quads;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
//        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
//            return Pair.of(this, ModelUtils.getTransform(0, 0, 0, 30, 90, 0, 0.625f).getMatrix());
//        }
//        return super.handlePerspective(cameraTransformType);
        return Pair.of(this, ModelUtils.getTransform(0, 0, 0, 30, 90, 0, 0.625f).getMatrix());
    }

    @Override
    public ItemOverrideList getOverrides() {
        return OVERRIDE;
    }
}
