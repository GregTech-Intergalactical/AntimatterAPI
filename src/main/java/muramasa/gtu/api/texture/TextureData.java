package muramasa.gtu.api.texture;

import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;

import java.util.List;

import static muramasa.gtu.api.texture.TextureMode.SINGLE;

public class TextureData {

    private TextureMode baseMode = SINGLE, overlayMode = SINGLE;
    private Texture[] base, overlay;
    private int tint = -1;

    public static TextureData get() {
        return new TextureData();
    }

    public TextureData base(Texture... base) {
        this.base = base;
//        if (base.length == 6) overlayMode = TextureMode.FULL;
        return this;
    }

    public TextureData overlay(Texture... overlay) {
        this.overlay = overlay;
//        if (overlay.length == 6) baseMode = TextureMode.FULL;
        return this;
    }

    public TextureData filterOverlay(int index) {
        overlay = new Texture[]{overlay[index]};
        return this;
    }

    public List<BakedQuad> apply(IBakedModel bakedModel) {
        return apply(bakedModel.getQuads(null, null, -1));
    }

    public List<BakedQuad> apply(List<BakedQuad> quads) {
        if (base != null) ModelUtils.tex(quads, baseMode, base, 0);
        if (overlay != null) ModelUtils.tex(quads, overlayMode, overlay, 1);
        return quads;
    }

    public TextureMode getBaseMode() {
        return baseMode;
    }

    public TextureMode getOverlayMode() {
        return overlayMode;
    }

    public Texture[] getBase() {
        return base;
    }

    public Texture[] getOverlay() {
        return overlay;
    }

    public int getTint() {
        return tint;
    }
}
