package muramasa.antimatter.cover;

public interface ICoverModeHandler {
    ICoverMode getCoverMode();
     int coverModeToInt();

     void setCoverMode(int index);

    default int getOverlayX() {
        return 176;
    }

    default int getOverlayY() {
        return 0;
    }
}
