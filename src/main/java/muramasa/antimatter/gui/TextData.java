package muramasa.antimatter.gui;

public class TextData {
    private String text;
    private int x, y;
    private int color = 0x555555;

    public TextData(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public TextData(String text, int x, int y, int color) {
        this(text, x, y);
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }
}
