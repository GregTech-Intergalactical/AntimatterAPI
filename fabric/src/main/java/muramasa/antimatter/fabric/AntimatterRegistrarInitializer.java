package muramasa.antimatter.fabric;

import muramasa.antimatter.MaterialDataInit;
import muramasa.antimatter.event.fabric.MaterialEvents;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;

public class AntimatterRegistrarInitializer implements IAntimatterRegistrarInitializer {
    @Override
    public void onRegistrarInit() {
        MaterialEvents.MATERIAL.register(MaterialDataInit::onMaterialEvent);
    }
}
