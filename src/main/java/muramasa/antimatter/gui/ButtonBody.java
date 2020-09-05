package muramasa.antimatter.gui;

public class ButtonBody {
    public static ButtonBody GREY = new ButtonBody("grey", 0, 0, 16, 0 );
    public static ButtonBody BLUE = new ButtonBody("blue", 32, 0, 48, 0);
    public static ButtonBody NO_HOVER = new ButtonBody("no_hover", 64, 0, 0, 0);

    protected String id;
    protected int xTexStart, yTexStart;
    protected int xDiffText, yDiffText;

    public ButtonBody(String id, int xTexStart, int yTexStart, int xDiffText, int yDiffText) {
        this.id = id;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.xDiffText = xDiffText;
        this.yDiffText = yDiffText;
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

    public int getDiffX() {
        return xDiffText;
    }

    public int getDiffY() {
        return yDiffText;
    }
}
