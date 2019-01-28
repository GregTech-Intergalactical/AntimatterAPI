package muramasa.itech.client.render.bakedmodels;

import muramasa.itech.common.blocks.BlockCables;
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

public class BakedModelCable extends BakedModelBase {

    private static HashMap<Integer, IBakedModel> maskToModelMap = new HashMap<>();
    private static HashMap<Integer, List<BakedQuad>> maskToQuadCache = new HashMap<>();

    private IBakedModel bakedBase;

    public BakedModelCable(IBakedModel[][] bakedConfigs) {
        this.bakedBase = bakedConfigs[0][0];

        //Default Mask
        maskToModelMap.put(0, bakedConfigs[0][0]);

        //Single Masks
        maskToModelMap.put(4, bakedConfigs[1][1]);
        maskToModelMap.put(8, bakedConfigs[1][0]);
        maskToModelMap.put(16, bakedConfigs[1][3]);
        maskToModelMap.put(32, bakedConfigs[1][2]);

        //Line Masks
        maskToModelMap.put(1, bakedConfigs[2][2]);
        maskToModelMap.put(2, bakedConfigs[2][2]);
        maskToModelMap.put(3, bakedConfigs[2][2]);
        maskToModelMap.put(12, bakedConfigs[2][0]);
        maskToModelMap.put(48, bakedConfigs[2][1]);

        //Cross Masks
        maskToModelMap.put(60, bakedConfigs[3][0]);

        //Side Masks
        maskToModelMap.put(28, bakedConfigs[4][3]);
        maskToModelMap.put(44, bakedConfigs[4][2]);
        maskToModelMap.put(52, bakedConfigs[4][0]);
        maskToModelMap.put(56, bakedConfigs[4][1]);
        maskToModelMap.put(49, bakedConfigs[4][4]);

        //Corner Masks
        maskToModelMap.put(20, bakedConfigs[5][3]);
        maskToModelMap.put(24, bakedConfigs[5][0]);
        maskToModelMap.put(36, bakedConfigs[5][1]);
        maskToModelMap.put(40, bakedConfigs[5][2]);
        maskToModelMap.put(5, bakedConfigs[5][7]);
        maskToModelMap.put(9, bakedConfigs[5][6]);
        maskToModelMap.put(6, bakedConfigs[5][5]);
        maskToModelMap.put(10, bakedConfigs[5][4]);
        maskToModelMap.put(18, bakedConfigs[5][8]);
        maskToModelMap.put(34, bakedConfigs[5][9]);
        maskToModelMap.put(33, bakedConfigs[5][11]);
        maskToModelMap.put(17, bakedConfigs[5][10]);
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();

        if (!(state instanceof IExtendedBlockState)) return quadList;

        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        int connections = extendedState.getValue(BlockCables.CONNECTIONS);

        IBakedModel bakedModel = maskToModelMap.get(connections);

        if (bakedModel != null) {
            if (maskToQuadCache.containsKey(connections)) {
                quadList.addAll(maskToQuadCache.get(connections));
            } else {
                List<BakedQuad> bakedQuads = maskToModelMap.get(connections).getQuads(extendedState, side, rand);
                maskToQuadCache.put(connections, bakedQuads);
                quadList.addAll(bakedQuads);
            }
        } else {
            quadList.addAll(bakedBase.getQuads(extendedState, side, rand));
        }

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return bakedBase.getOverrides();
    }
}
