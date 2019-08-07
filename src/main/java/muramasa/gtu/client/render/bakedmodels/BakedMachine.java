package muramasa.gtu.client.render.bakedmodels;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.overrides.ItemOverrideMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import static muramasa.gtu.api.properties.GTProperties.*;

public class BakedMachine extends BakedBase {

    public static IBakedModel BASE;
    public static IBakedModel[][] OVERLAYS;
    public static IBakedModel[] OVERLAY_EMPTY;
    public static Object2ObjectOpenHashMap<String, IBakedModel> COVERS;
    public static ItemOverrideMachine itemOverride = new ItemOverrideMachine();

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        IExtendedBlockState exState = (IExtendedBlockState) state;
        int type = exState.getValue(TYPE);
        int facing = exState.getValue(FACING);
        TextureData data = exState.getValue(TEXTURE);
        Cover[] covers = exState.getValue(COVER);

        if (covers != null) {
            for (int s = 0; s < 6; s++) {
                if (!covers[s].isEmpty()) {
                    quads.addAll(covers[s].onRender(this, getCovers(covers[s], s, state), s));
                } else {
                    quads.addAll(getOverlays(type, s, data.getOverlay(), state));
                }
            }
        } else {
            for (int s = 0; s < 6; s++) {
                quads.addAll(getOverlays(type, s, data.getOverlay(), state));
            }
        }

        ModelUtils.tex(quads, data.getBaseMode(), data.getBase(), 0); //Machine Base
        ModelUtils.tex(quads, data.getBaseMode(), data.getBase(), 3); //Cover Base
//        texOverlays(quads, data.getOverlayMode(), data.getOverlay());
        quads = ModelUtils.trans(quads, facing);

        return quads;
    }

    public List<BakedQuad> getOverlays(int t, int s, Texture[] data, IBlockState state) {
        return OVERLAYS[t][s] != null ? ModelUtils.tex(OVERLAYS[t][s].getQuads(state, null, -1), 1, data[s]) : OVERLAY_EMPTY[s].getQuads(state, null, -1);
    }

    public List<BakedQuad> getCovers(Cover cover, int s, IBlockState state) {
        return ModelUtils.trans(COVERS.get(cover.getId()).getQuads(state, null, -1), s);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
