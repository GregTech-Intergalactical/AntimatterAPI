package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

public class BakedModelMachineItem extends BakedModelBase {

    public static IBakedModel[] OVERLAYS;

    private Machine type;
    private Tier tier;

    public BakedModelMachineItem(Machine type, Tier tier) {
        this.type = type;
        this.tier = tier;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return tex(OVERLAYS[type.getInternalId()].getQuads(state, side, rand), 0, type.getBaseTextures(tier)[0]);
    }
}
