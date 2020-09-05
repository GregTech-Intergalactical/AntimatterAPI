package muramasa.antimatter.gui;

public class ButtonOverlay {

    public static ButtonOverlay STOP = new ButtonOverlay("stop", 0, 32, 32, 32);
    public static ButtonOverlay TORCH_OFF = new ButtonOverlay("torch_off", 32, 32, 32, 32);
    public static ButtonOverlay TORCH_ON = new ButtonOverlay("torch_on", 64, 32, 32, 32);
    public static ButtonOverlay ACCEPT = new ButtonOverlay("accept", 96, 32, 32, 32);
    public static ButtonOverlay EXIT = new ButtonOverlay("exit", 128, 32, 32, 32);
    public static ButtonOverlay WHITELIST = new ButtonOverlay("whitelist", 160, 32, 32, 32);
    public static ButtonOverlay BLACKLIST = new ButtonOverlay("blacklist", 192, 32, 32, 32);
    public static ButtonOverlay PERCENTAGE = new ButtonOverlay("percentage", 224, 32, 32, 32);
    public static ButtonOverlay OUT_GREEN = new ButtonOverlay("out_green", 0, 64, 32, 32);
    public static ButtonOverlay IN_BLUE = new ButtonOverlay("in_blue", 32, 64, 32, 32);
    public static ButtonOverlay IN_GREEN = new ButtonOverlay("in_green", 64, 64, 32, 32);
    public static ButtonOverlay IN_RED = new ButtonOverlay("in_red", 96, 64, 32, 32);
    public static ButtonOverlay MINUS = new ButtonOverlay("minus", 128, 64, 32, 32);
    public static ButtonOverlay DIV = new ButtonOverlay("div", 160, 64, 32, 32);
    public static ButtonOverlay PLUS = new ButtonOverlay("plus", 192, 64, 32, 32);
    public static ButtonOverlay MULT = new ButtonOverlay("mult", 224, 64, 32, 32);

    protected String id;
    protected int x, y, w, h;

    public ButtonOverlay(String id, int x, int y, int w, int h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public String getId() {
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
}

