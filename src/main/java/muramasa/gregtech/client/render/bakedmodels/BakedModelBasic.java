package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.properties.GTProperties;
import muramasa.gregtech.client.render.overrides.ItemOverrideBasic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BakedModelBasic extends BakedModelBase {

    protected IBakedModel bakedModel;
    protected ItemOverrideBasic itemOverride;

    public BakedModelBasic(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
        this.itemOverride = new ItemOverrideBasic(bakedModel);
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (!(state instanceof IExtendedBlockState) || side != null) return Collections.emptyList();
        IExtendedBlockState exState = (IExtendedBlockState) state;
        List<BakedQuad> quads = new LinkedList<>();

        if (hasUnlistedProperty(exState, GTProperties.TEXTURE)) {
            quads.addAll(bakedModel.getQuads(state, null, rand));
        }

        return quads;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
