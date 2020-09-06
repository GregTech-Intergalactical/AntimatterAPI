package muramasa.antimatter.gui;

public class ButtonBody extends ButtonOverlay {

    public static ButtonBody GREY = new ButtonBody("grey", 0, 0, 16, 0, 16, 16);
    public static ButtonBody BLUE = new ButtonBody("blue", 32, 0, 16, 0, 16, 16);
    public static ButtonBody NO_HOVER = new ButtonBody("no_hover", 64, 0, 0, 0, 16, 16);
    public static ButtonBody APAD_LEFT = new ButtonBody("apad_left", 1, 49, 0, 16, 14, 14);
    public static ButtonBody PAD_LEFT = new ButtonBody("pad_left", 17, 49, 0, 16, 14, 14);
    public static ButtonBody PAD_RIGHT = new ButtonBody("pad_right", 33, 49, 0, 16, 14, 14);
    public static ButtonBody APAD_RIGHT = new ButtonBody("apad_right", 49, 49, 0, 16, 14, 14);

    protected int x2, y2; // Difference

    public ButtonBody(String id, int x, int y, int x2, int y2, int w, int h) {
        super(id, x, y, w, h);
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }
}
