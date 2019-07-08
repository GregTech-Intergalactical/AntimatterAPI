package muramasa.gtu.client.render.bakedmodels;

import muramasa.gtu.api.data.Materials;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class BakedOreItem extends BakedBase {

    private int material, materialType;
    private String stoneType;

    public BakedOreItem(int material, String stoneType, int materialType) {
        this.material = material;
        this.stoneType = stoneType;
        this.materialType = materialType;
    }

    //TODO cache with material hash + stoneType hash + oreType hash
    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        quads.addAll(BakedOre.STONES.get(stoneType).getQuads(state, side, rand));
        quads.addAll(BakedOre.OVERLAYS[materialType][Materials.get(material).getSet().getInternalId()].getQuads(state, side, rand));
        return quads;
    }
}
