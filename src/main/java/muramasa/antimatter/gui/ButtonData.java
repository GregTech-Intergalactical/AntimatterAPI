package muramasa.antimatter.gui;

public class ButtonData {

    public int id, x, y, w, h;
    public String text = "X";

    public ButtonData(int id, int x, int y, int w, int h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public ButtonData(int id, int x, int y, int w, int h, String text) {
        this(id, x, y, w, h);
        this.text = text;
    }
}
