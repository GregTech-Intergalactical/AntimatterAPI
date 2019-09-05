package muramasa.gtu.client.render.bakedmodels;

import muramasa.gtu.api.blocks.BlockCT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BakedCT extends BakedBase {

    private BlockCT block;

    public BakedCT(BlockCT block) {
        this.block = block;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null) return Collections.emptyList();
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState exState = (IExtendedBlockState) state;
            int ct = exState.getValue(BlockCT.CT);



            IBakedModel baked = block.getLookup().get(ct);
            if (baked != null) return baked.getQuads(state, side, rand);
        }
        return block.getBaked().getQuads(state, side, rand);
    }
}
