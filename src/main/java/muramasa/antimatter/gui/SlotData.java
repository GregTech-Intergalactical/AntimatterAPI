package muramasa.antimatter.gui;

public class SlotData {

    public SlotType type;
    public int x, y, data;

    public SlotData(SlotType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public SlotData(SlotType type, int x, int y, int data) {
        this(type, x, y);
        this.data = data;
    }
}
