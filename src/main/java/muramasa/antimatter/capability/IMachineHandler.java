package muramasa.antimatter.capability;

import muramasa.antimatter.machine.event.IMachineEvent;

public interface IMachineHandler {

    default void onMachineEvent(IMachineEvent event, Object... data) {
        //NOOP
    }
}
