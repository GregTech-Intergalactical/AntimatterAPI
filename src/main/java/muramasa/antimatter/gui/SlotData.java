package muramasa.antimatter.gui;

public class SlotData {

    private SlotType type;
    private int x;
    private int y;
    private int data = -1;

    public SlotData(SlotType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public SlotData(SlotType type, int x, int y, int data) {
        this(type, x, y);
        this.data = data;
    }

    public SlotType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getData() {
        return data;
    }
}
