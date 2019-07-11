package muramasa.gtu.client.render.bakedmodels;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import static muramasa.gtu.api.properties.GTProperties.ROCK_MODEL;

public class BakedRock extends BakedBase {

    public static IBakedModel[] BAKED;

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        if (state == null) return quads;

        int model = ((IExtendedBlockState) state).getValue(ROCK_MODEL);

        return BAKED[model].getQuads(state, side, rand);
    }
}
