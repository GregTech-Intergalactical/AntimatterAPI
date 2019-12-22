package muramasa.gtu.api.gui;

import muramasa.gtu.api.util.int2;
import muramasa.gtu.api.util.int4;

public enum BarDir {

    TOP(new int4(176, 0, 20, 18), new int2(72, 18)),
    BOTTOM(new int4(176, 0, 20, 18), new int2(72, 18)),
    LEFT(new int4(176, 0, 20, 18), new int2(72, 18)),
    RIGHT(new int4(176, 0, 20, 18), new int2(72, 18));

    private int4 uv;
    private int2 pos;

    BarDir(int4 uv, int2 pos) {
        this.uv = uv;
        this.pos = pos;
    }

    public int4 getUV() {
        return uv;
    }

    public int2 getPos() {
        return pos;
    }
}
