package muramasa.antimatter.capability;

import muramasa.antimatter.gui.event.IGuiEvent;

public interface IGuiHandler {

    default void onGuiEvent(IGuiEvent event, int... data) {
        //NOOP
    }
}
