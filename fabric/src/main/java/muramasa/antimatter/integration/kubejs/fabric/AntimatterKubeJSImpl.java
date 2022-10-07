package muramasa.antimatter.integration.kubejs.fabric;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import muramasa.antimatter.worldgen.fabric.AntimatterFabricWorldgen;

public class AntimatterKubeJSImpl {
    public static void onRegister(){
        AntimatterRegistration.onRegister();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        CommonHandler.setup();
        AntimatterFabricWorldgen.init();
    }
}
