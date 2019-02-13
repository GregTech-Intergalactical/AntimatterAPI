package muramasa.gregtech.api.machines;

public class Slot {

    //TODO change type to enum
    //type: 0 = input, 1 = output, 2 = fluidInput, 3 = fluidOutput
    public int type, x, y, d;

    public Slot(int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public Slot(int type, int x, int y, int d) {
        this(type, x, y);
        this.d = d;
    }
}
