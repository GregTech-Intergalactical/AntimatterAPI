package muramasa.antimatter.integration.kubejs.fabric;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.fabric.AntimatterImpl;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import muramasa.antimatter.worldgen.fabric.AntimatterFabricWorldgen;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;

public class AntimatterKubeJSImpl {
    public static void onRegister(){
        new AntimatterImpl().initialize(true);
    }
}
