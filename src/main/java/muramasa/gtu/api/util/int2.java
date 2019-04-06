package muramasa.gtu.api.util;

public class int2 {

    public int x;
    public int y;

    public int2() {

    }

    public int2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
