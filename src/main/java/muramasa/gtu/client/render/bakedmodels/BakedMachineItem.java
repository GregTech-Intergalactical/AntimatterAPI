package muramasa.gtu.client.render.bakedmodels;

import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

public class BakedMachineItem extends BakedBase {

    public static IBakedModel[] OVERLAYS;

    private Machine type;
    private Tier tier;

    public BakedMachineItem(Machine type, Tier tier) {
        this.type = type;
        this.tier = tier;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return ModelUtils.tex(OVERLAYS[type.getInternalId()].getQuads(state, side, rand), 0, type.getBaseTexture(tier));
    }
}
