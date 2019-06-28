package muramasa.gtu.api.worldgen;

public enum DimensionType {

    OVERWORLD(0),
    NETHER(-1),
    END(1),
    MOON(-99), //TODO FIND ID
    MARS(-99), //TODO FIND ID
    ASTEROID(-30); //TODO VALIDATE ID

    private int id;

    DimensionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
