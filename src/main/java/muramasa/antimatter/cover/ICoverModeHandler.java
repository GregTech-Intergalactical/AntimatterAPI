package muramasa.antimatter.cover;

public interface ICoverModeHandler {
    ICoverMode getCoverMode();

    default int getOverlayX(){
        return 176;
    }

    default int getOverlayY(){
        return 0;
    }
}
