package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MachineBakedModel extends CoveredBakedModel {

    public MachineBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        List<BakedQuad> quads = super.getBlockQuads(state, side, rand, data);
        //IBakedModel testModel = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(Ref.ID + ":machine/cover/basic"));
        //quads.addAll(testModel.getQuads(state, side, rand, data));

        //ModelResourceLocation loc = new ModelResourceLocation(Ref.ID + ":machine/cover/basic");
        //IBakedModel testModel = ModelLoader.instance().getBakedModel(loc, ModelRotation.X0_Y0, ModelLoader.defaultTextureGetter());
        //if (testModel != null) quads.addAll(testModel.getQuads(state, side, rand, data));

        return quads;
    }
}
