package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.properties.GTProperties;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.client.render.overrides.ItemOverrideMachine;
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

public class BakedModelMachine extends BakedModelBase {

    public static IBakedModel BAKED, OVERLAY_EMPTY;
    public static IBakedModel[][] OVERLAYS;
    public static IBakedModel[] COVERS;
    public static ItemOverrideMachine itemOverride = new ItemOverrideMachine();

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (!(state instanceof IExtendedBlockState) || side != null) return Collections.emptyList();
        IExtendedBlockState exState = (IExtendedBlockState) state;
        List<BakedQuad> quads = new LinkedList<>();
        int type = exState.getValue(GTProperties.TYPE);
        int facing = exState.getValue(GTProperties.FACING);
        TextureData data = exState.getValue(GTProperties.TEXTURE);

        if (hasUnlistedProperty(exState, GTProperties.COVER)) {
            Cover[] covers = exState.getValue(GTProperties.COVER);
            for (int s = 0; s < 6; s++) {
                if (!covers[s].isEmpty()) {
                    quads.addAll(covers[s].onRender(getCovers(covers[s], s, state), s));
                } else {
                    quads.addAll(getOverlays(type, s, data.getOverlay(), state));
                }
            }
        } else {
            for (int s = 0; s < 6; s++) {
                quads.addAll(getOverlays(type, s, data.getOverlay(), state));
            }
        }

        tex(quads, data.getBaseMode(), data.getBase(), 0);
        if (facing > 2) quads = trans(quads, facing);

        return quads;
    }

    public List<BakedQuad> getOverlays(int t, int s, Texture[] data, IBlockState state) {
        return OVERLAYS[t][s] != null ? tex(OVERLAYS[t][s].getQuads(state, null, -1), 1, data[s]) : trans(OVERLAY_EMPTY.getQuads(state, null, -1), s);
    }

    public List<BakedQuad> getCovers(Cover cover, int s, IBlockState state) {
        return trans(COVERS[cover.getInternalId()].getQuads(state, null, -1), s);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
