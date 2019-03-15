package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.properties.GTProperties;
import muramasa.gregtech.api.texture.TextureData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BakedTextureData extends BakedBase {

    protected IBakedModel baked;
    protected ItemOverrideList item;
    protected TextureData data;

    public BakedTextureData(IBakedModel baked, ItemOverrideList item) {
        this.baked = baked;
        this.item = item;
    }

    public BakedTextureData(IBakedModel baked, TextureData data) {
        this.baked = baked;
        this.data = data;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IExtendedBlockState exState, @Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (exState != null) return exState.getValue(GTProperties.TEXTURE).apply(baked);
        return data != null ? data.apply(baked) : Collections.emptyList();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return item;
    }
}
