package muramasa.antimatter.integration.kubejs.fabric;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;

public class AntimatterKubeJSImpl {
    public static void onRegister(){
        AntimatterRegistration.onRegister();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
    }
}
