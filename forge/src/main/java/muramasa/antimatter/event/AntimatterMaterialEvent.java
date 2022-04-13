package muramasa.antimatter.event;

import muramasa.antimatter.material.MaterialEvent;
import muramasa.antimatter.registration.IAntimatterRegistrar;

public class AntimatterMaterialEvent extends AntimatterEvent {
    MaterialEvent materialEvent;
    public AntimatterMaterialEvent(IAntimatterRegistrar registrar, MaterialEvent materialEvent) {
        super(registrar);
        this.materialEvent = materialEvent;
    }
}
