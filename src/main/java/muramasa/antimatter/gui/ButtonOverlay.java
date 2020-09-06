package muramasa.antimatter.gui;

public class ButtonOverlay {

    public static ButtonOverlay STOP = new ButtonOverlay("stop", 0, 16, 16, 16);
    public static ButtonOverlay TORCH_OFF = new ButtonOverlay("torch_off", 16, 16, 16, 16);
    public static ButtonOverlay TORCH_ON = new ButtonOverlay("torch_on", 32, 16, 16, 16);
    public static ButtonOverlay EXPORT = new ButtonOverlay("export", 48, 16, 16, 16);
    public static ButtonOverlay IMPORT = new ButtonOverlay("import", 64, 16, 16, 16);
    public static ButtonOverlay INPUT_OFF = new ButtonOverlay("input_off", 80, 16, 16, 16);
    public static ButtonOverlay LESS = new ButtonOverlay("less", 96, 16, 16, 16);
    public static ButtonOverlay EQUAL = new ButtonOverlay("equal", 112, 16, 16, 16);
    public static ButtonOverlay MORE = new ButtonOverlay("more", 128, 16, 16, 16);
    public static ButtonOverlay WHITELIST = new ButtonOverlay("whitelist", 144, 16, 16, 16);
    public static ButtonOverlay BLACKLIST = new ButtonOverlay("blacklist", 160, 16, 16, 16);
    public static ButtonOverlay MINUS = new ButtonOverlay("minus", 176, 16, 16, 16);
    public static ButtonOverlay PLUS = new ButtonOverlay("plus", 192, 16, 16, 16);
    public static ButtonOverlay DIVISION = new ButtonOverlay("division", 208, 16, 16, 16);
    public static ButtonOverlay MULT = new ButtonOverlay("mult", 224, 16, 16, 16);
    public static ButtonOverlay PERCENT = new ButtonOverlay("percent", 240, 16, 16, 16);
    public static ButtonOverlay ARROW_LEFT = new ButtonOverlay("arrow_left", 0, 32, 16, 16);
    public static ButtonOverlay A_LEFT = new ButtonOverlay("a_left", 16, 32, 16, 16);
    public static ButtonOverlay A_RIGHT = new ButtonOverlay("a_right", 32, 32, 16, 16);
    public static ButtonOverlay ARROW_RIGHT = new ButtonOverlay("arrow_right", 48, 32, 16, 16);

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

