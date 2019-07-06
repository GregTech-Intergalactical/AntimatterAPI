package muramasa.gtu.client.render.bakedmodels;

import muramasa.gtu.api.materials.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class BakedOreItem extends BakedBase {

    private int material, stoneType, materialType;

    public BakedOreItem(int material, int stoneType, int materialType) {
        this.material = material;
        this.stoneType = stoneType;
        this.materialType = materialType;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        quads.addAll(BakedOre.STONES[stoneType].getQuads(state, side, rand));
        quads.addAll(BakedOre.OVERLAYS.get(materialType)[Material.get(material).getSet().getInternalId()].getQuads(state, side, rand));
        return quads;
    }
}
