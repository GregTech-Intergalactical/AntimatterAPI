package muramasa.antimatter.cover;

public interface ICoverModeHandler {
    ICoverMode getCoverMode(CoverStack<?> stack);

    default int getOverlayX(){
        return 176;
    }

    default int getOverlayY(){
        return 0;
    }
}
