package muramasa.itech.client.model.bakedmodels;

import muramasa.itech.client.model.overrides.ItemOverrideHatch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedModelHatch extends BakedModelBase {

    private HashMap<String, IBakedModel> bakedModels;
    private ItemOverrideHatch overrideList;

    public BakedModelHatch(HashMap<String, IBakedModel> bakedModels) {
        this.bakedModels = bakedModels;
        overrideList = new ItemOverrideHatch(bakedModels);
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();

        if (!(state instanceof IExtendedBlockState)) return quadList;

        IExtendedBlockState extendedState = (IExtendedBlockState) state;

//        System.out.println(extendedState.getValue(BlockHatches.TEXTURE));

//        quadList.addAll(bakedModels.get(extendedState.getValue(BlockHatches.TEXTURE)).getQuads(state, side, rand));
//        quadList.addAll(bakedModels.get(extendedState.getClean().getValue(BlockHatches.TEXTURE).getName()).getQuads(state, side, rand));

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }
}
