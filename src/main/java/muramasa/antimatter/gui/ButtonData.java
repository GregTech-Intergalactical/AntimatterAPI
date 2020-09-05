package muramasa.antimatter.gui;

import net.minecraft.util.ResourceLocation;

public class ButtonData {

    private int id;
    private int x;
    private int y;
    private int w;
    private int h;
    private ButtonBody body;
    private ButtonOverlay overlay;

    public ButtonData(int id, int x, int y, int w, int h, ButtonBody body, ButtonOverlay overlay) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.body = body;
        this.overlay = overlay;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public ButtonBody getBody() {
        return body;
    }

    public ButtonOverlay getOverlay() {
        return overlay;
    }
}
