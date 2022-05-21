package muramasa.antimatter.event.forge;

import muramasa.antimatter.material.MaterialEvent;

public class AntimatterMaterialEvent extends AntimatterEvent {
    MaterialEvent materialEvent;
    public AntimatterMaterialEvent(IAntimatterRegistrar registrar, MaterialEvent materialEvent) {
        super(registrar);
        this.materialEvent = materialEvent;
    }
}
