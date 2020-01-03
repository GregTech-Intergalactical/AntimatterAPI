package muramasa.antimatter.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.QuadLayer;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoader;

import java.util.List;

import static muramasa.antimatter.texture.TextureMode.SINGLE;

public class TextureData {

    private TextureMode baseMode = SINGLE, overlayMode = SINGLE;
    private Texture[] base, overlay;
    private int tint = -1;

    public static TextureData get() {
        return new TextureData();
    }

    public TextureData base(Texture... base) {
        this.base = base;
        if (base.length == 6) {
            baseMode = TextureMode.FULL;
        }
        return this;
    }

    public TextureData overlay(Texture... overlay) {
        this.overlay = overlay;
        //TODO this breaks machine quad retex
        //if (overlay.length == 6) baseMode = TextureMode.FULL;
        return this;
    }

    public TextureData filterOverlay(int index) {
        overlay = new Texture[]{overlay[index]};
        return this;
    }

    public List<BakedQuad> apply(IBakedModel bakedModel) {
        return apply(bakedModel.getQuads(null, null, Ref.RNG));
    }

    public List<BakedQuad> apply(List<BakedQuad> quads) {
        if (base != null) ModelUtils.tex(quads, baseMode, base, QuadLayer.BASE);
        if (overlay != null) ModelUtils.tex(quads, overlayMode, overlay, QuadLayer.OVERLAY);
        return quads;
    }

    public IBakedModel bakeAsItem() {
        IModel model = new ItemLayerModel(ImmutableList.<ResourceLocation>builder().add(base).add(overlay).build());
        return model.bake(GregTech.PROXY.getModelBakery(), ModelLoader.defaultTextureGetter(), new BasicState(model.getDefaultState(), false), DefaultVertexFormats.ITEM);
    }

    public IBakedModel bakeAsBlock() {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        for (int i = 0; i < base.length; i++) {
            builder.put("" + i, base[i].toString());
        }
        IModel model = GregTech.PROXY.getModelBakery().getUnbakedModel(ModelUtils.MODEL_BASIC_LOC).retexture(builder.build());
        return model.bake(GregTech.PROXY.getModelBakery(), ModelLoader.defaultTextureGetter(), new BasicState(model.getDefaultState(), false), DefaultVertexFormats.BLOCK);
//        if (hasOverlay()) {
//            return ModelUtils.texBake(ModelUtils.MODEL_LAYERED, new String[]{"0", "1"}, new Texture[]{getBase(0), getOverlay(0)});
//        } else {
//            switch (getBaseMode()) {
//                case SINGLE:
//                    return ModelUtils.texBake(ModelUtils.MODEL_BASIC, "0", base[0]);
//                case FULL:
//                    return ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, new String[]{"0", "1", "2", "3", "4", "5"}, new Texture[]{base[0], base[1], base[2], base[3], base[4], base[5]});
//                default:
//                    return ModelUtils.BAKED_MISSING;
//            }
//        }
    }

    public TextureMode getBaseMode() {
        return baseMode;
    }

    public TextureMode getOverlayMode() {
        return overlayMode;
    }

    public Texture getBase(int layer) {
        return base[layer];
    }

    public Texture getOverlay(int layer) {
        return overlay[layer];
    }

    public Texture[] getBase() {
        return base;
    }

    public Texture[] getOverlay() {
        return overlay;
    }

    public boolean hasBase() {
        return base != null && base.length > 0;
    }

    public boolean hasOverlay() {
        return overlay != null && overlay.length > 0;
    }

    public int getTint() {
        return tint;
    }
}
