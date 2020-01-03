package muramasa.antimatter.client;

public enum QuadLayer {

    BASE(0),
    OVERLAY(1),
    EXTRA(2),

    COVER_BASE(3),
    COVER_OVERLAY(4),
    COVER_EXTRA(5);

    private int index;

    QuadLayer(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
