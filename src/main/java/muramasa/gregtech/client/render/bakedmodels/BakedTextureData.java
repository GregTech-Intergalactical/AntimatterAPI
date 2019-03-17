package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.properties.GTProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;

public class BakedTextureData extends BakedBase {

    protected IBakedModel baked;
    protected ItemOverrideList item;

    public BakedTextureData(IBakedModel baked, ItemOverrideList item) {
        this.baked = baked;
        this.item = item;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return ((IExtendedBlockState) state).getValue(GTProperties.TEXTURE).apply(baked);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return item;
    }
}
