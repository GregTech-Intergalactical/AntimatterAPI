package muramasa.gtu.api.gui;

public enum GuiUpdateType {

    PROGRESS(1.0f),
    MACHINE_STATE(1.0f);

    private float updateThreshold;

    GuiUpdateType(float updateThreshold) {
        this.updateThreshold = updateThreshold;
    }

    public float getUpdateThreshold() {
        return updateThreshold;
    }
}
