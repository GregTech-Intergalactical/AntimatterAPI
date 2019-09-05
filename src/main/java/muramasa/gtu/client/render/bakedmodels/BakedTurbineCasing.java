package muramasa.gtu.client.render.bakedmodels;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.gtu.api.blocks.BlockTurbineCasing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BakedTurbineCasing extends BakedBase {

    public static Int2ObjectOpenHashMap<List<BakedQuad>> LOOKUP = new Int2ObjectOpenHashMap<>();
    private IBakedModel baked;

    public BakedTurbineCasing(IBakedModel baked) {
        this.baked = baked;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null) return Collections.emptyList();
        List<BakedQuad> quads = new ArrayList<>(baked.getQuads(state, side, rand));
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState exState = (IExtendedBlockState) state;
            int[] ct = exState.getValue(BlockTurbineCasing.CT);
            List<BakedQuad> overlays;
            for (int i = 0; i < ct.length; i++) {
                if (ct[i] > 0) {
                    overlays = LOOKUP.get(ct[i]);
                    if (overlays != null) quads.addAll(overlays);
                }
            }
        }
        return quads;
    }
}
