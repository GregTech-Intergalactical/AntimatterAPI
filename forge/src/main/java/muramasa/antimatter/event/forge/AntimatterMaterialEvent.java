package muramasa.antimatter.event.forge;

import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.registration.IAntimatterRegistrar;

public class AntimatterMaterialEvent extends AntimatterEvent {
    MaterialEvent materialEvent;
    public AntimatterMaterialEvent(IAntimatterRegistrar registrar, MaterialEvent materialEvent) {
        super(registrar);
        this.materialEvent = materialEvent;
    }

    public MaterialEvent getMaterialEvent() {
        return materialEvent;
    }
}
