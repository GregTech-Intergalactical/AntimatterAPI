package muramasa.gtu.client.render.bakedmodels;

import muramasa.antimatter.client.baked.BakedBase;
import muramasa.gtu.client.render.overrides.ItemOverrideNichrome;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BakedNichrome extends BakedBase {

    public BakedNichrome(IBakedModel bakedModel) {
        super(bakedModel);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {

        //bakedModel = ModelUtils.getBakedFromState(Blocks.BEDROCK.getDefaultState());

        if (bakedModel != null) {
            return bakedModel.getQuads(state, side, rand, data);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideNichrome();
    }
}
