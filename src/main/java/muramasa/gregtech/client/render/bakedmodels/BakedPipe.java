package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.client.render.overrides.ItemOverridePipe;
import muramasa.gregtech.common.blocks.BlockPipe;
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
        }

        return quads;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return OVERRIDE;
    }
}
