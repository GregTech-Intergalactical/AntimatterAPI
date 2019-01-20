package muramasa.itech.client.model.bakedmodels;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class BakedModelBaseMulti extends BakedModelBase {

    private IBakedModel[] bakedModels;

    public BakedModelBaseMulti(IBakedModel... bakedModels) {
        this.bakedModels = bakedModels;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        for (int i = 0; i < bakedModels.length; i++) {
            if (bakedModels[i] != null) {
                quads.addAll(bakedModels[i].getQuads(state, side, rand));
            }
        }
        return quads;
    }
}
