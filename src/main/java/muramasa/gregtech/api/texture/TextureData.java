package muramasa.gregtech.api.texture;

import static muramasa.gregtech.api.texture.TextureMode.*;

public class TextureData {

    private TextureMode baseMode, overlayMode;
    private Texture[] base, overlay;
    private int tint = -1;

    public TextureData(Texture... base) {
        this(base, new Texture[0]);
    }

    public TextureData(Texture[] base, Texture[] overlay) {
        baseMode = base.length == 1 ? SINGLE : base.length == 5 ? COPIED_SIDES : FULL;
        overlayMode = overlay.length == 1 ? SINGLE : overlay.length == 5 ? COPIED_SIDES : FULL;
        this.base = base;
        this.overlay = overlay;
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

    public void setBase(Texture... textures) {
        base = textures;
    }

    public void setOverlay(Texture... textures) {
        overlay = textures;
    }

    public boolean hasOverlays() {
        return overlay != null && overlay.length > 0;
    }
}
