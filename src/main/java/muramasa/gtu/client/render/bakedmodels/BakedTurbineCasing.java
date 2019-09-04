package muramasa.gtu.client.render.bakedmodels;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import muramasa.gtu.api.blocks.BlockCasingTurbine;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BakedTurbineCasing extends BakedBase {

    private static Int2IntOpenHashMap LOOKUP = new Int2IntOpenHashMap();

    static {
        LOOKUP.put(216, 3); //North
        LOOKUP.put(232, 5);
        LOOKUP.put(201, 1);
        LOOKUP.put(202, 7);

        LOOKUP.put(332, 3); //South
        LOOKUP.put(316, 5);
        LOOKUP.put(301, 1);
        LOOKUP.put(302, 7);

        LOOKUP.put(408, 3); //West
        LOOKUP.put(404, 5);
        LOOKUP.put(401, 1);
        LOOKUP.put(402, 7);

        LOOKUP.put(504, 3); //East
        LOOKUP.put(508, 5);
        LOOKUP.put(501, 1);
        LOOKUP.put(502, 7);

        LOOKUP.put(217, 0); //North Corners
        LOOKUP.put(233, 2);
        LOOKUP.put(218, 6);
        LOOKUP.put(234, 8);

        LOOKUP.put(333, 0); //South Corners
        LOOKUP.put(317, 2);
        LOOKUP.put(334, 6);
        LOOKUP.put(318, 8);

        LOOKUP.put(409, 0); //West Corners
        LOOKUP.put(405, 2);
        LOOKUP.put(410, 6);
        LOOKUP.put(406, 8);

        LOOKUP.put(505, 0); //East Corners
        LOOKUP.put(509, 2);
        LOOKUP.put(506, 6);
        LOOKUP.put(510, 8);
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null) return Collections.emptyList();
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState exState = (IExtendedBlockState) state;
            int ctm = exState.getValue(BlockCasingTurbine.CTM);
            int tileFacing = exState.getValue(BlockCasingTurbine.TILE_FACING);
            Texture[] set = ctm < 1000 ? Textures.LARGE_TURBINE_ACTIVE : Textures.LARGE_TURBINE_ACTIVE;

            Texture base = ((BlockCasingTurbine) state.getBlock()).getData().getBase(0);
            List<BakedQuad> baseQuads = ModelUtils.tex(ModelUtils.MODEL_COMPLEX, new String[]{"0", "6", "7", "8", "9", "10", "11"}, new Texture[]{base, base, base, base, base, base, base}).bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter()).getQuads(state, side, rand);
            return ModelUtils.tex(baseQuads, tileFacing + 6, set[LOOKUP.get(ctm)]);
        }
        return ModelUtils.getBakedTextureData(((BlockCasingTurbine) state.getBlock()).getData()).getQuads(state, side, rand);
    }
}
