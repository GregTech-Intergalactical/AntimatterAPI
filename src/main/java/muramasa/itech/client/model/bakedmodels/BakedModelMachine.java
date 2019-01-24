package muramasa.itech.client.model.bakedmodels;

import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.client.model.overrides.ItemOverrideMachine;
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

public class BakedModelMachine extends BakedModelBase {

    private HashMap<String, IBakedModel[]> bakedModels;
    private IBakedModel[][] bakedCovers;
    private ItemOverrideMachine overrideList;

    public BakedModelMachine(HashMap<String, IBakedModel[]> bakedModels, HashMap<String, IBakedModel> bakedModelsItem, IBakedModel[][] bakedCovers) {
        this.bakedModels = bakedModels;
        this.bakedCovers = bakedCovers;
        this.overrideList = new ItemOverrideMachine(bakedModelsItem);
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();

        if (!(state instanceof IExtendedBlockState)) return quadList;

        IExtendedBlockState exState = (IExtendedBlockState) state;

        int facing = exState.getClean().getValue(ITechProperties.FACING).getIndex() - 2;
        String type = exState.getValue(ITechProperties.TYPE), tier = exState.getValue(ITechProperties.TIER);

        if (type != null && !type.isEmpty() && tier != null && !tier.isEmpty()) {
            quadList.addAll(bakedModels.get(type + tier)[facing].getQuads(state, side, rand));
        }

        CoverType[] covers = exState.getValue(ITechProperties.COVERS);
        if (covers != null) {
            for (int i = 0; i < covers.length; i++) {
                if (covers[i] != CoverType.NONE) {
                    quadList.addAll(bakedCovers[covers[i].ordinal()][i].getQuads(exState, side, rand));
                }
            }
        }

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }
}
