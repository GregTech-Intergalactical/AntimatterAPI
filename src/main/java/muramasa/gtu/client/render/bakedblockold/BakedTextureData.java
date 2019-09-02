package muramasa.gtu.client.render.bakedblockold;

import com.google.common.collect.Lists;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.client.render.bakedmodels.BakedBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;

@Deprecated
public class BakedTextureData extends BakedBase {

    protected IBakedModel baked;
    protected ItemOverrideList item;

    public BakedTextureData(IBakedModel baked, ItemOverrideList item) {
        this.baked = baked;
        this.item = item;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        //Note: using Lists.newArrayList to avoid altering the quads of other models. Fixes the long standing multi-texture casing bug
        return ((IExtendedBlockState) state).getValue(GTProperties.TEXTURE).apply(Lists.newArrayList(baked.getQuads(state, side, rand)));
    }

    @Override
    public ItemOverrideList getOverrides() {
        return item;
    }
}
