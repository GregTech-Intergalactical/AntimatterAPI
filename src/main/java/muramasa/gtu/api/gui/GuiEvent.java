package muramasa.gtu.api.gui;

public enum GuiEvent {

    PROGRESS(1.0f),
    MACHINE_STATE(1.0f);

    private float updateThreshold;

    GuiEvent(float updateThreshold) {
        this.updateThreshold = updateThreshold;
    }

    public float getUpdateThreshold() {
        return updateThreshold;
    }
}
