package muramasa.antimatter.gui;

public class ButtonOverlay {
    public static ButtonOverlay STOP = new ButtonOverlay("stop", 0, 16);
    public static ButtonOverlay TORCH_OFF = new ButtonOverlay("torch_off", 16, 16);
    public static ButtonOverlay TORCH_ON = new ButtonOverlay("torch_on", 32, 16);
    public static ButtonOverlay ACCEPT = new ButtonOverlay("accept", 48, 16);
    public static ButtonOverlay EXIT = new ButtonOverlay("exit", 64, 16);
    public static ButtonOverlay WHITELIST = new ButtonOverlay("whitelist", 80, 16);
    public static ButtonOverlay BLACKLIST = new ButtonOverlay("blacklist", 96, 16);
    public static ButtonOverlay PERCENTAGE = new ButtonOverlay("percentage", 112, 16);
    public static ButtonOverlay OUT_GREEN = new ButtonOverlay("out_green", 0, 32);
    public static ButtonOverlay IN_BLUE = new ButtonOverlay("in_blue", 16, 32);
    public static ButtonOverlay IN_GREEN = new ButtonOverlay("in_green", 32, 32);
    public static ButtonOverlay IN_RED = new ButtonOverlay("in_red", 48, 32);
    public static ButtonOverlay MINUS = new ButtonOverlay("minus", 64, 32);
    public static ButtonOverlay DIV = new ButtonOverlay("div", 80, 32);
    public static ButtonOverlay PLUS = new ButtonOverlay("plus", 96, 32);
    public static ButtonOverlay MULT = new ButtonOverlay("mult", 112, 32);

    protected String id;
    protected int xTexStart, yTexStart;

    public ButtonOverlay(String id, int xTexStart, int yTexStart) {
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
    }

    public String getId() {
        return id;
    }

    public int getTexX() {
        return xTexStart;
    }

    public int getTexY() {
        return yTexStart;
    }
}

