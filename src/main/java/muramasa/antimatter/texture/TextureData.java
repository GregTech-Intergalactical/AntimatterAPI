package muramasa.antimatter.texture;

//TODO this entire thing either needs removed or rethought
public class TextureData {

    private Texture[] base, overlay;
    private int tint = -1;

    public TextureData base(Texture... base) {
        this.base = base;
        return this;
    }

    public TextureData overlay(Texture... overlay) {
        this.overlay = overlay;
        return this;
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
