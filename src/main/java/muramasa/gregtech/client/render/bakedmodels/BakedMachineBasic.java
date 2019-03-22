package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.properties.GTProperties;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.client.render.ModelUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import static muramasa.gregtech.client.render.ModelUtils.trans;

public class BakedMachineBasic extends BakedMachine {

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        int facing = exState.getValue(GTProperties.FACING);
        TextureData data = exState.getValue(GTProperties.TEXTURE);

        List<BakedQuad> quads = new LinkedList<>(BASE.getQuads(state, side, rand));
        ModelUtils.tex(quads, data.getBaseMode(), data.getBase(), 0);

//        Cover[] covers;
//        List<BakedQuad> coverQuads = null;
//        if (Utils.hasUnlistedProperty(exState, GTProperties.COVER) && (covers = exState.getValue(GTProperties.COVER)) != null) {
//            Texture[] overlays = data.getOverlay();
//            for (int i = 0; i < covers.length; i++) {
//                if (!covers[i].isEmpty()) {
////                    overlays[i].setEmpty();
//                    coverQuads = Utils.trans(COVER[covers[i].getInternalId()].getQuads(state, side, rand), i);
//                    if (covers[i].retextureToMachineTier()) {
//                        Utils.tex(coverQuads, 0, RenderHelper.getSprite(Tier.getTransform(exState.getValue(GTProperties.TIER)).getBaseTexture()));
//                    }
//                    coverQuads.addAll(covers[i].onRender(coverQuads));
//                }
//            }
//            Utils.tex(quads, data.getOverlayMode(), overlays, 1);
//        } else if (data.hasOverlays()) {
//            Utils.tex(quads, data.getOverlayMode(), data.getOverlay(), 1);
//        }

        if (facing > 2) quads = ModelUtils.trans(quads, facing);
//        if (coverQuads != null) quads.addAll(coverQuads);

        return quads;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
