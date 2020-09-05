package muramasa.antimatter.gui;

public class ButtonBody extends ButtonOverlay {

    public static ButtonBody GREY = new ButtonBody("grey", 0, 0, 32, 0, 32, 32);
    public static ButtonBody BLUE = new ButtonBody("blue", 64, 0, 32, 0, 32, 32);
    public static ButtonBody NO_HOVER = new ButtonBody("no_hover", 128, 0, 0, 0, 32, 32);

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
