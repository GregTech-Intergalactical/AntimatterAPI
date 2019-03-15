package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.texture.TextureData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BakedModelTextureData extends BakedModelBase {

    private IBakedModel bakedModel;
    private TextureData data;

    public BakedModelTextureData(IBakedModel bakedModel, TextureData data) {
        this.bakedModel = bakedModel;
        this.data = data;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null) return Collections.emptyList();
        List<BakedQuad> quads = new LinkedList<>(bakedModel.getQuads(state, null, rand));
        tex(quads, data.getBaseMode(), data.getBase(), 0);
        if (data.hasOverlays()) {
            tex(quads, data.getOverlayMode(), data.getOverlay(), 1);
        }
        return quads;
    }
}
